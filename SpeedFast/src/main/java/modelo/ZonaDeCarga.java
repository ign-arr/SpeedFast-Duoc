package modelo;

import java.util.ArrayList;

public class ZonaDeCarga {

    private ArrayList<Pedido> pedidos;

    public ZonaDeCarga() {
        pedidos = new ArrayList<>();

        System.out.println("[Zona de carga inicializada]");
        System.out.println();
    }

    public synchronized void agregarPedido(Pedido pedido) {

        pedidos.add(pedido);

        System.out.println(
                "Pedido #" + pedido.getIdPedido()
                        + " agregado. Destino: "
                        + pedido.getDireccionEntrega()
        );
    }

    public synchronized Pedido retirarPedido() {

        if (pedidos.isEmpty()) {
            return null;
        }

        return pedidos.remove(0);
    }
}