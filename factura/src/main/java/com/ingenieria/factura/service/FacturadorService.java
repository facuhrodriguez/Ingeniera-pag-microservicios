package com.ingenieria.factura.service;

import com.ingenieria.factura.domain.DetalleFactura;
import com.ingenieria.factura.domain.Factura;
import com.ingenieria.factura.repository.DetalleFacturaRepository;
import com.ingenieria.factura.repository.FacturaRepository;
import com.ingenieria.factura.service.dto.ordencompra.OrdenCompraDTO;

import java.time.LocalDate;

import com.ingenieria.factura.service.httpclient.ordencompra.FacturadorHttpClient;
import com.ingenieria.factura.web.rest.errors.BadRequestAlertException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class FacturadorService {

    private final FacturaRepository facturaRepository;
    private final DetalleFacturaRepository detalleFacRepository;
    private final FacturadorHttpClient facturadorHttpClient;
    private final double IVA = 21.0;
    private final Logger log = LoggerFactory.getLogger(FacturadorService.class);

    private final double descuento = 20.0;
    private final float cantidadParaDescuento = 15f;

    public FacturadorService(
            FacturaRepository facturaRepository,
            DetalleFacturaRepository detFacturaRepository, FacturadorHttpClient facturadorHttpClient) {
        this.facturaRepository = facturaRepository;
        this.detalleFacRepository = detFacturaRepository;
        this.facturadorHttpClient = facturadorHttpClient;
    }

    public Factura run(Long idCliente, OrdenCompraDTO ordenCompraDTO) throws BadRequestAlertException {
        log.info("Facturador service: el cliente {} desea generar una orden de compra, con lo siguiente. " + "{}", idCliente, ordenCompraDTO);

        Factura factura = new Factura();

        // almacenar una factura incompleta
        factura.setIva(IVA);
        factura.setIdCliente(idCliente);
        factura.setFecha(LocalDate.now());

        factura = facturaRepository.save(factura).block();

        // enviar al ms producto que chequee el stock completo
        // retorna un id de solicitud para decrementar ESE stock
        long idSolicitud = facturadorHttpClient.checkAllStock(ordenCompraDTO);

        // decrementar el stock enviado asincronamente
        Mono<String> req = facturadorHttpClient.decreaseAllStock(idSolicitud);
        req.subscribe((str) -> log.debug("Facturador service: stock decrementado!"));

        // obtener precios de todos los productos
        JSONObject listaProductoPrecio = facturadorHttpClient.getPrices(ordenCompraDTO.getListProductsOnly().toString());

        // una vez obtenida la información necesaria, crear la factura!
        Factura finalFactura = factura;
        Double precioFinal = ordenCompraDTO
            .getProductoCantidadList()
            .stream()
            .map(productoCantidadDTO -> {
                Long codigoProducto = productoCantidadDTO.getId();

                float cantidad = productoCantidadDTO.getCantidad();

                DetalleFactura detalleFactura = new DetalleFactura();

                // crear y almacenar el detalle factura del producto
                detalleFactura.setCantidad(cantidad);
                detalleFactura.setIdProducto(codigoProducto);
                detalleFactura.setFactura(finalFactura);
                // almacenar de forma asíncrona, bloqueando el hilo hasta que retorne la entidad
                DetalleFactura detalleFacturaSaved = detalleFacRepository.save(detalleFactura).block();
                assert finalFactura != null;
                finalFactura.addDetalleFactura(detalleFacturaSaved);

                // calculo de precio total
                double precio = (Double) listaProductoPrecio.get(String.valueOf(codigoProducto)) * productoCantidadDTO.getCantidad();

                return calcularDescuento(precio, cantidad);
            })
            .reduce(0.0, Double::sum, Double::sum);

        // almacenar la factura completando los datos que le faltaban
        assert factura != null;
        factura.setTotalSinIva(precioFinal);
        factura.setTotalConIva(this.calcularPrecioConIVA(precioFinal));
        factura = facturaRepository.save(factura).block();

        log.debug("Facturador service: factura creada {}", factura);

        return factura;
    }

    private double calcularPrecioConIVA(double precio) {
        return precio + ((precio * IVA) / 100);
    }

    private double calcularDescuento(double precio, float cantidad) {
        if (cantidad > this.cantidadParaDescuento) {
            return precio - (precio * this.descuento / 100);
        }
        return precio;
    }

}
