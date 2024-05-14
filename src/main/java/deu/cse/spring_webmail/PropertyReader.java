/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *
 * @author Prof.Jong Min Lee
 */
@Component
@Slf4j
public class PropertyReader {

    private Properties sysProps;
    private Properties dbProps;

    public PropertyReader() {
        SysProperties();
        DbProperties();
    }
    
    private void SysProperties() {
        this.sysProps = PropertyRead("/system.properties");
    }

    private void DbProperties() {
        this.dbProps = PropertyRead("/application-db.properties");
    }
    
    private Properties PropertyRead(String propertyFile) {
        Properties props = new Properties();
        try (Reader reader = new InputStreamReader(
                this.getClass().getResourceAsStream(propertyFile))) {
            props.load(reader);
            log.debug("props = {}", props);
        } catch (IOException e) {
            log.error("PropertyReader: 예외 발생 = {}", e.getMessage());
        }
        return props;
    }

    public String getSysProperty(String propertyName) {
        return sysProps.getProperty(propertyName);
    }
    
    public String getDBProperty(String propertyName) {
        return dbProps.getProperty(propertyName);
    }
}