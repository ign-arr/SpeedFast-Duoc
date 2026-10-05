package modelo;

public class PedidoEncomienda extends Pedido {

    public PedidoEncomienda(int idPedido, String direccionEntrega, int distanciaKm) {
        super(idPedido, direccionEntrega, distanciaKm);
    }

    @Override
    public void asignarRepartidor() {
        setNombreRepartidor("Camila Soto");

        System.out.println("Asignando repartidor...");
        System.out.println("→ Validando peso y embalaje... OK");
        System.out.println("→ Pedido asignado a " + getNombreRepartidor());
    }

    @Override
    public int calcularTiempoEntrega() {
        double tiempo = 20 + (1.5 * getDistanciaKm());

        return (int) tiempo;
    }
}