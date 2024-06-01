/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.control;

import deu.cse.spring_webmail.entity.Inbox;
import deu.cse.spring_webmail.model.Pop3Agent;
import deu.cse.spring_webmail.model.RegistarManager;
import deu.cse.spring_webmail.model.RegistarRow;
import deu.cse.spring_webmail.model.UserAdminAgent;
import deu.cse.spring_webmail.model.AddrBookManager;
import deu.cse.spring_webmail.model.AddrBookRow;
import deu.cse.spring_webmail.model.DeleteManager;
import deu.cse.spring_webmail.model.FindManager;
import deu.cse.spring_webmail.model.FindRow;
import deu.cse.spring_webmail.model.UserManager;
import deu.cse.spring_webmail.model.UserRow;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import javax.imageio.ImageIO;

import deu.cse.spring_webmail.repository.InboxRepository;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 초기 화면과 관리자 기능(사용자 추가, 삭제)에 대한 제어기
 *
 * @author skylo
 */
@Controller
@PropertySource("classpath:/system.properties")
@PropertySource("classpath:/config.properties")
@PropertySource("classpath:/application-db.properties")
@Slf4j
public class SystemController {

    @Autowired
    private ServletContext ctx;
    @Autowired
    private HttpSession session;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private Environment env;

    @Value("${root.id}")
    private String ROOT_ID;
    @Value("${root.password}")
    private String ROOT_PASSWORD;
    @Value("${admin.id}")
    private String ADMINISTRATOR;  //  = "admin";
    @Value("${james.control.port}")
    private Integer JAMES_CONTROL_PORT;
    @Value("${james.host}")
    private String JAMES_HOST;
    @Value("${mysql.server.ip}")
    private String mysqlServerIp;
    @Value("${mysql.server.port}")
    private String mysqlServerPort;

    @Autowired
    private InboxRepository inboxRepository;

    @GetMapping("/login")
    public String index() {
        log.debug("index() called...");
        session.setAttribute("host", JAMES_HOST);
        session.setAttribute("debug", "false");

        return "index";
    }

    @RequestMapping(value = "/login.do", method = {RequestMethod.GET, RequestMethod.POST})
    public String loginDo(@RequestParam Integer menu) {
        String url = "";
        log.debug("로그인 처리: menu = {}", menu);
        switch (menu) {
            case CommandType.LOGIN:
                String host = (String) request.getSession().getAttribute("host");
                String userid = request.getParameter("username");
                String password = request.getParameter("password");

                // Check the login information is valid using <<model>>Pop3Agent.
                Pop3Agent pop3Agent = new Pop3Agent(host, userid, password);
                boolean isLoginSuccess = pop3Agent.validate();

                // Now call the correct page according to its validation result.
                if (isLoginSuccess) {
                    if (isAdmin(userid)) {
                        // HttpSession 객체에 userid를 등록해 둔다.
                        session.setAttribute("userid", userid);
                        // response.sendRedirect("admin_menu.jsp");
                        url = "redirect:/admin_menu";
                    } else {
                        // HttpSession 객체에 userid와 password를 등록해 둔다.
                        session.setAttribute("userid", userid);
                        session.setAttribute("password", password);
                        // response.sendRedirect("main_menu.jsp");
                        url = "redirect:/main_menu";  // URL이 http://localhost:8080/webmail/main_menu 이와 같이 됨.
                        // url = "/main_menu";  // URL이 http://localhost:8080/webmail/login.do?menu=91 이와 같이 되어 안 좋음
                    }
                } else {
                    // RequestDispatcher view = request.getRequestDispatcher("login_fail.jsp");
                    // view.forward(request, response);
                    url = "redirect:/login_fail";
                }
                break;
            case CommandType.LOGOUT:
                session.invalidate();
                url = "redirect:/login";  // redirect: 반드시 넣어야만 컨텍스트 루트로 갈 수 있음
                break;
            default:
                break;
        }
        return url;
    }

    @GetMapping("/login_fail")
    public String loginFail() {
        return "login_fail";
    }

    protected boolean isAdmin(String userid) {
        boolean status = false;

        if (userid.equals(this.ADMINISTRATOR)) {
            status = true;
        }

        return status;
    }

    @GetMapping("/main_menu")
    public String mainMenu(Model model) {
        Pop3Agent pop3 = new Pop3Agent();
        pop3.setHost((String) session.getAttribute("host"));
        pop3.setUserid((String) session.getAttribute("userid"));
        pop3.setPassword((String) session.getAttribute("password"));

        String messageList = pop3.getMessageList();

        // 페이징 처리
        List<Inbox> dataRows = inboxRepository.findByRepositoryName(session.getAttribute("userid").toString());

        log.info("dataRows 총 개수 = {}", dataRows.size());

        int rows = dataRows.size(); // 전체 레코드 수
        int recordsPerPage = 5; // 페이지 당 보여줄 메일 수
        int totalPages = (int) Math.ceil((double) rows / recordsPerPage); // 전체 페이지 수 계산

        log.info("dataRows 총 개수 = {}", dataRows.size());

        model.addAttribute("messageList", messageList);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("recordsPerPage", recordsPerPage);

        return "main_menu";
    }

    @GetMapping("/admin_menu")
    public String adminMenu(Model model) {
        log.debug("root.id = {}, root.password = {}, admin.id = {}",
                ROOT_ID, ROOT_PASSWORD, ADMINISTRATOR);

        model.addAttribute("userList", getUserList());
        return "admin/admin_menu";
    }

    @GetMapping("/add_user")
    public String addUser() {
        return "admin/add_user";
    }

    @PostMapping("/add_user.do")
    public String addUserDo(@RequestParam String id, @RequestParam String password,
            RedirectAttributes attrs) {
        log.debug("add_user.do: id = {}, password = {}, port = {}",
                id, password, JAMES_CONTROL_PORT);

        try {
            String cwd = ctx.getRealPath(".");
            UserAdminAgent agent = new UserAdminAgent(JAMES_HOST, JAMES_CONTROL_PORT, cwd,
                    ROOT_ID, ROOT_PASSWORD, ADMINISTRATOR);

            // if (addUser successful)  사용자 등록 성공 팦업창
            // else 사용자 등록 실패 팝업창
            if (agent.addUser(id, password)) {
                attrs.addFlashAttribute("msg", String.format("사용자(%s) 추가를 성공하였습니다.", id));
            } else {
                attrs.addFlashAttribute("msg", String.format("사용자(%s) 추가를 실패하였습니다.", id));
            }
        } catch (Exception ex) {
            log.error("add_user.do: 시스템 접속에 실패했습니다. 예외 = {}", ex.getMessage());
        }

        return "redirect:/admin_menu";
    }

    @GetMapping("/addrbook")
    public String addrbookMenu(Model model) {
        model.addAttribute("mysql_server_ip", this.mysqlServerIp);
        model.addAttribute("mysql_server_port", this.mysqlServerPort);
        log.info("mysql.server.ip = {}, mysql.server.port = {}", this.mysqlServerIp, this.mysqlServerPort);
        return "addrbook/addrbook_menu";
    }

    @GetMapping("/add_addr")
    public String addAddr(Model model) {
        model.addAttribute("mysql_server_ip", this.mysqlServerIp);
        model.addAttribute("mysql_server_port", this.mysqlServerPort);
        log.info("mysql.server.ip = {}, mysql.server.port = {}", this.mysqlServerIp, this.mysqlServerPort);
        return "addrbook/add_addr";
    }

    @PostMapping("/add_addr.do")
    public String addAddrDo(@RequestParam String username, @RequestParam String addrname,
            Model model) {
        model.addAttribute("mysql_server_ip", this.mysqlServerIp);
        model.addAttribute("mysql_server_port", this.mysqlServerPort);
        log.info("mysql.server.ip = {}, mysql.server.port = {}", this.mysqlServerIp, this.mysqlServerPort);
        String userName = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");
        String jdbcDriver = env.getProperty("spring.datasource.driver-class-name");
        AddrBookManager manager = new AddrBookManager(mysqlServerIp, mysqlServerPort, userName, password, jdbcDriver);

        manager.addRow(username, addrname);

        List<AddrBookRow> dataRows = manager.getAllRows((String) session.getAttribute("userid"));
        model.addAttribute("dataRows", dataRows);

        return "redirect:/addrbook";
    }
    
    @PostMapping("/del_addr.do")
    public String delAddrDo(@RequestParam String username, @RequestParam String addrname,
            Model model) {
        model.addAttribute("mysql_server_ip", this.mysqlServerIp);
        model.addAttribute("mysql_server_port", this.mysqlServerPort);
        log.info("mysql.server.ip = {}, mysql.server.port = {}", this.mysqlServerIp, this.mysqlServerPort);
        String userName = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");
        String jdbcDriver = env.getProperty("spring.datasource.driver-class-name");
        AddrBookManager manager = new AddrBookManager(mysqlServerIp, mysqlServerPort, userName, password, jdbcDriver);

        manager.delRow(username, addrname);

        List<AddrBookRow> dataRows = manager.getAllRows((String) session.getAttribute("userid"));
        model.addAttribute("dataRows", dataRows);

        return "redirect:/addrbook";
    }

    @GetMapping("/delete_user")
    public String deleteUser(Model model) {
        log.debug("delete_user called");
        model.addAttribute("userList", getUserList());
        return "admin/delete_user";
    }

    /**
     *
     * @param selectedUsers <input type=checkbox> 필드의 선택된 이메일 ID. 자료형: String[]
     * @param attrs
     * @return
     */
    @PostMapping("delete_user.do")
    public String deleteUserDo(@RequestParam String[] selectedUsers, RedirectAttributes attrs) {
        String userName = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");
        String jdbcDriver = env.getProperty("spring.datasource.driver-class-name");

        log.debug("delete_user.do: selectedUser = {}", List.of(selectedUsers));

        DeleteManager manager = new DeleteManager(mysqlServerIp, mysqlServerPort, userName, password, jdbcDriver);
        manager.deleteRow(selectedUsers);

        try {
            String cwd = ctx.getRealPath(".");
            UserAdminAgent agent = new UserAdminAgent(JAMES_HOST, JAMES_CONTROL_PORT, cwd,
                    ROOT_ID, ROOT_PASSWORD, ADMINISTRATOR);
            agent.deleteUsers(selectedUsers);  // 수정!!!
        } catch (Exception ex) {
            log.error("delete_user.do : 예외 = {}", ex);
        }

        return "redirect:/admin_menu";
    }

    private List<String> getUserList() {
        String cwd = ctx.getRealPath(".");
        UserAdminAgent agent = new UserAdminAgent(JAMES_HOST, JAMES_CONTROL_PORT, cwd,
                ROOT_ID, ROOT_PASSWORD, ADMINISTRATOR);
        List<String> userList = agent.getUserList();
        log.debug("userList = {}", userList);

        //(주의) root.id와 같이 '.'을 넣으면 안 됨.
        userList.sort((e1, e2) -> e1.compareTo(e2));
        return userList;
    }

    @GetMapping("/img_test")
    public String imgTest() {
        return "img_test/img_test";
    }

    /**
     * https://34codefactory.wordpress.com/2019/06/16/how-to-display-image-in-jsp-using-spring-code-factory/
     *
     * @param imageName
     * @return
     */
    @RequestMapping(value = "/get_image/{imageName}")
    @ResponseBody
    public byte[] getImage(@PathVariable String imageName) {
        try {
            String folderPath = ctx.getRealPath("/WEB-INF/views/img_test/img");
            return getImageBytes(folderPath, imageName);
        } catch (Exception e) {
            log.error("/get_image 예외: {}", e.getMessage());
        }
        return new byte[0];
    }

    private byte[] getImageBytes(String folderPath, String imageName) {
        ByteArrayOutputStream byteArrayOutputStream;
        BufferedImage bufferedImage;
        byte[] imageInByte;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            bufferedImage = ImageIO.read(new File(folderPath + File.separator + imageName));
            String format = imageName.substring(imageName.lastIndexOf(".") + 1);
            ImageIO.write(bufferedImage, format, byteArrayOutputStream);
            byteArrayOutputStream.flush();
            imageInByte = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            return imageInByte;
        } catch (FileNotFoundException e) {
            log.error("getImageBytes 예외: {}", e.getMessage());
        } catch (Exception e) {
            log.error("getImageBytes 예외: {}", e.getMessage());
        }
        return null;
    }

    @GetMapping("/Registar")
    public String insertTable(Model model) {
        String userName = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");
        String jdbcDriver = env.getProperty("spring.datasource.driver-class-name");
        log.debug("ip = {}, port = {}", this.mysqlServerIp, this.mysqlServerPort);

        RegistarManager manager = new RegistarManager(mysqlServerIp, mysqlServerPort, userName, password, jdbcDriver);
        List<RegistarRow> dataRows = manager.getAllRows();
        model.addAttribute("dataRows", dataRows);

        return "Registar/insert_userinfo"; //페이지를 index로 설정하면 등록된 유저 정보 확인 가능(수정 필요)
    }

    @GetMapping("/insert_userinfo")
    public String insertUserInfo() {
        return "Registar/insert_userinfo";
    }

    @PostMapping("/insert")
    public String insertUserInfo(@RequestParam String rid, @RequestParam String rpw, @RequestParam String name, @RequestParam String phone, Model model, RedirectAttributes attrs) {
        String userName = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");
        String jdbcDriver = env.getProperty("spring.datasource.driver-class-name");

        log.debug("add_user.do: id = {}, password = {}, port = {}",
                rid, rpw, JAMES_CONTROL_PORT);

        try {
            String cwd = ctx.getRealPath(".");
            UserAdminAgent agent = new UserAdminAgent(JAMES_HOST, JAMES_CONTROL_PORT, cwd,
                    ROOT_ID, ROOT_PASSWORD, ADMINISTRATOR);

            if (agent.addUser(rid, rpw)) {
                attrs.addFlashAttribute("msg", String.format("아이디(%s) 회원가입이 완료되었습니다.", rid));
                RegistarManager manager = new RegistarManager(mysqlServerIp, mysqlServerPort, userName, password, jdbcDriver);
                manager.addRow(rid, rpw, name, phone);
                List<RegistarRow> dataRows = manager.getAllRows();
                model.addAttribute("dataRows", dataRows);
            } else {
                attrs.addFlashAttribute("msg", String.format("아이디(%s) 회원가입이 실패하였습니다.", rid));
            }
        } catch (Exception ex) {
            log.error("add_user.do: 시스템 접속에 실패했습니다. 예외 = {}", ex.getMessage());
        }

        return "redirect:/login";
    }

    @GetMapping("/Find")
    public String findTable(Model model) {
        String userName = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");
        String jdbcDriver = env.getProperty("spring.datasource.driver-class-name");

        FindManager manager = new FindManager(mysqlServerIp, mysqlServerPort, userName, password, jdbcDriver);

        return "Find/find_id";
    }

    @GetMapping("/find_id")
    public String findUserInfo() {
        return "redirect:/login";
    }

    @PostMapping("/find")
    public String findUserInfo(@RequestParam String name, @RequestParam String phone, Model model) {
        String userName = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");
        String jdbcDriver = env.getProperty("spring.datasource.driver-class-name");

        FindManager manager = new FindManager(mysqlServerIp, mysqlServerPort, userName, password, jdbcDriver);
        String username = manager.FindRow(name, phone);

        if (username != null) {
            model.addAttribute("username", username);
            return "Find/find_id_success";
        } else {
            model.addAttribute("name", name);
            return "Find/find_id_fail";
        }
    }

//    @GetMapping("/user")
//    public String userTable(Model model) {
//        model.addAttribute("mysql_server_ip", this.mysqlServerIp);
//        model.addAttribute("mysql_server_port", this.mysqlServerPort);
//        log.info("mysql.server.ip = {}, mysql.server.port = {}", this.mysqlServerIp, this.mysqlServerPort);
//        return "user/user";
//    }
    @GetMapping("/user")
    public String updateTable(Model model) {
        String userName = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");
        String jdbcDriver = env.getProperty("spring.datasource.driver-class-name");
        log.debug("ip = {}, port = {}", this.mysqlServerIp, this.mysqlServerPort);

        UserManager manager = new UserManager(mysqlServerIp, mysqlServerPort, userName, password, jdbcDriver, session);
        List<UserRow> dataRows = manager.getAllRows((String) session.getAttribute("userid"));
        model.addAttribute("dataRows", dataRows);

        return "user/update_userinfo"; //페이지를 index로 설정하면 등록된 유저 정보 확인 가능(수정 필요)
    }

    @GetMapping("/update_userinfo")
    public String updateUserInfo() {
        return "user/update_userinfo";
    }

    @PostMapping("/update")
    public String updateUserInfo(@RequestParam String rid, @RequestParam String rpw, @RequestParam String name, @RequestParam String phone, Model model, RedirectAttributes attrs, HttpSession session) {
        String userName = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");
        String jdbcDriver = env.getProperty("spring.datasource.driver-class-name");

        log.debug("add_user.do: id = {}, password = {}, port = {}",
                rid, rpw, JAMES_CONTROL_PORT);

        try {
            String cwd = ctx.getRealPath(".");
            UserAdminAgent agent = new UserAdminAgent(JAMES_HOST, JAMES_CONTROL_PORT, cwd,
                    ROOT_ID, ROOT_PASSWORD, ADMINISTRATOR);

            if (agent.addUser(rid, rpw)) {
                attrs.addFlashAttribute("msg", String.format("비밀번호가 바뀌었습니다.", rid));
                UserManager manager = new UserManager(mysqlServerIp, mysqlServerPort, userName, password, jdbcDriver, session);
                manager.addRow(rid, rpw, name, phone);
                // List<UserRow> dataRows = manager.getAllRows();
                List<UserRow> dataRows = manager.getAllRows((String) session.getAttribute("userid"));
                model.addAttribute("dataRows", dataRows);
            } else {
                attrs.addFlashAttribute("msg", String.format("비밀번호 수정에 실패하였습니다.", rid));
            }
        } catch (Exception ex) {
            log.error("add_user.do: 시스템 접속에 실패했습니다. 예외 = {}", ex.getMessage());
        }

        return "redirect:/main_menu";
    }
    
    
    @PostMapping("/delete")
    public String deleteUserInfo(@RequestParam String[] selectedUsers, RedirectAttributes attrs) {
        String userName = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");
        String jdbcDriver = env.getProperty("spring.datasource.driver-class-name");

        
        log.debug("delete_user.do: selectedUser = {}", List.of(selectedUsers));

        try {
            String cwd = ctx.getRealPath(".");
            UserAdminAgent agent = new UserAdminAgent(JAMES_HOST, JAMES_CONTROL_PORT, cwd,
                    ROOT_ID, ROOT_PASSWORD, ADMINISTRATOR);
            agent.deleteUsers(selectedUsers);
            System.out.println("User withdrawn in successfully.");
        } catch (Exception ex) {
            log.error("delete_user.do: 예외 = {}", ex);
        }

        return "redirect:/main_menu";
    }
}