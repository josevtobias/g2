package dashboard;

import javax.inject.Named;
import javax.enterprise.context.Dependent;

@Named(value = "dashboard")
@Dependent

public class dashboard {
    private String enBodega = "None";
    private String enCamino = "None";
    private String entregado = "None"; 

    public String getEnBodega() {
        return enBodega;
    }

    public String getEnCamino() {
        return enCamino;
    }

    public String getEntregado() {
        return entregado;
    }

    
    
    
}
