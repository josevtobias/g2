/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package g2.web.bean;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author victor
 */
@Named(value = "beanSesionUsuario")
@SessionScoped
public class BeanSesionUsuario implements Serializable {


    /**
     * Creates a new instance of BeanSesionUsuario
     */
    public BeanSesionUsuario() {
    }

    
    
}
