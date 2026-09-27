package modelo;

public class PedidoComida extends Pedido {

    public PedidoComida(int idPedido, String direccionEntrega, int distanciaKm) {
        super(idPedido, direccionEntrega, distanciaKm);
    }

    @Override
    public void asignarRepartidor() {
        setNombreRepartidor("Luis Díaz");

        System.out.println("Asignando repartidor...");
        System.out.println("→ Verificando mochila térmica... OK");
        System.out.println("→ Pedido asignado a " + getNombreRepartidor());
    }

    @Override
    public int calcularTiempoEntrega() {
        return 15 + (2 * getDistanciaKm());
    }
}