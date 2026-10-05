package modelo;

public class PedidoExpress extends Pedido {

    public PedidoExpress(int idPedido, String direccionEntrega, int distanciaKm) {
        super(idPedido, direccionEntrega, distanciaKm);
    }

    @Override
    public void asignarRepartidor() {
        setNombreRepartidor("Roberto Guzmán");

        System.out.println("Asignando repartidor...");
        System.out.println(
                "→ Repartidor más cercano con disponibilidad inmediata encontrado."
        );
        System.out.println("→ Pedido asignado a " + getNombreRepartidor());
    }

    @Override
    public int calcularTiempoEntrega() {

        int tiempo = 10;

        if (getDistanciaKm() > 5) {
            tiempo = tiempo + 5;
        }

        return tiempo;
    }
}