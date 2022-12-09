package com.ingenieria.factura.service;

import com.ingenieria.factura.domain.DetalleFactura;
import com.ingenieria.factura.domain.Factura;
import com.ingenieria.factura.repository.DetalleFacturaRepository;
import com.ingenieria.factura.repository.FacturaRepository;
import com.ingenieria.factura.service.dto.getprecio.ProductoListDTO;
import com.ingenieria.factura.service.dto.ordencompra.OrdenCompraDTO;
import com.ingenieria.factura.service.httpclient.ordencompra.FacturadorHttpClient;
import com.ingenieria.factura.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

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

    public Mono<Factura> run(Long idCliente, OrdenCompraDTO ordenCompraDTO) throws BadRequestAlertException {
        log.info("Facturador service: el cliente {} desea generar una orden de compra, con lo siguiente. {}", idCliente, ordenCompraDTO);

        // almacenar una factura incompleta
        Factura factura = new Factura();
        factura.setIva(IVA);
        factura.setIdCliente(idCliente);
        factura.setFecha(LocalDate.now());

        // enviar al ms producto que chequee el stock completo
        return facturadorHttpClient.checkAllStock(ordenCompraDTO)
            // retorna un id de solicitud para decrementar ESE stock
            .doOnSuccess(facturadorHttpClient::decrementarStock)
            // obtener precios de todos los productos
            .then(facturadorHttpClient.getPrices(new ProductoListDTO(ordenCompraDTO.getListProductsOnly())))
            .flatMap((response) -> {

                ArrayList<DetalleFactura> dfs = new ArrayList<>();

                HashMap<Long, Float> prices = response.getPrecios();
                double precioFinal = ordenCompraDTO.getProductoCantidadList()
                    .stream()
                    .peek(productoCantidadDTO -> {
                        long codigoProducto = productoCantidadDTO.getId();
                        float cantidad = productoCantidadDTO.getCantidad();

                        // crear y almacenar el detalle factura del producto
                        DetalleFactura detalleFactura = new DetalleFactura();
                        detalleFactura.setCantidad(cantidad);
                        detalleFactura.setIdProducto(codigoProducto);
                        detalleFactura.setFactura(factura);
                        dfs.add(detalleFactura);
                    })
                    .map((productoCantidadDTO) -> {
                        long codigoProducto = productoCantidadDTO.getId();
                        float cantidad = productoCantidadDTO.getCantidad();
                        double precioUnitario = prices.get(codigoProducto);

                        // calculo de precio parcial y total
                        double precioTemp = precioUnitario * cantidad;

                        return calcularDescuento(precioTemp, cantidad);
                    })
                    .reduce(0.0, Double::sum, Double::sum);

                factura.setTotalSinIva(precioFinal);
                factura.setTotalConIva(this.calcularPrecioConIVA(precioFinal));

                return facturaRepository.save(factura)
                    .flatMap(factura1 ->
                        Flux.fromIterable(dfs)
                            .flatMap(detalleFacRepository::save)
                            .doOnNext(factura::addDetalleFactura)
                            .doOnComplete(() -> log.debug("Facturador service: detalle facturas creadas"))
                            .then(Mono.just(factura1))
                            .doOnSuccess((factura2) -> log.debug("Facturador service: factura creada {}", factura2))
                    );
            });
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
