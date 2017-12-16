package dashboard;

import javax.faces.bean.ManagedBean;
import javax.annotation.PostConstruct;
import java.io.Serializable;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

@ManagedBean (name = "barra")
public class chart implements Serializable{
    private BarChartModel barModel;

    @PostConstruct
    public void init(){
         createBarModels();
    }

    public BarChartModel getBarModel() {
        return barModel;
    }

    private void createBarModels() {
        createBarModel();
    }
     
    private void createBarModel() {
        barModel = initBarModel();
         
        barModel.setTitle("Productos más vendidos");
        barModel.setLegendPosition("ne");
         
        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Nombre");
         
        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Cantidad");
        yAxis.setMin(0);
        yAxis.setMax(1000);
    } 
    private BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();
 
        ChartSeries producto = new ChartSeries();
        producto.setLabel("Producto");
        producto.set("2004", 120);
        producto.set("2005", 100);
        producto.set("2006", 44);
        producto.set("2007", 150);
        producto.set("2008", 25);
 
        model.addSeries(producto);
       
        return model;
    }
}
