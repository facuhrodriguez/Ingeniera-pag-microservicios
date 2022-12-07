package com.ingenieria.factura.service;

import com.ingenieria.factura.domain.DetalleFactura;
import com.ingenieria.factura.domain.Factura;
import com.ingenieria.factura.repository.DetalleFacturaRepository;
import com.ingenieria.factura.repository.FacturaRepository;
import com.ingenieria.factura.service.dto.ordencompra.OrdenCompraDTO;
import com.ingenieria.factura.service.dto.ordencompra.ProductoCantidadDTO;
import com.ingenieria.factura.service.httpclient.ordencompra.FacturadorHttpClient;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class FacturadorServiceTest {

    @Mock
    private FacturaRepository facturaRepository;

    @Mock
    private DetalleFacturaRepository detalleFacRepository;

    @Mock
    private FacturadorHttpClient facturadorHttpClient;

    @InjectMocks
    private FacturadorService facturadorService;

    private double IVA;
    private double descuento;
    private float cantidadParaDescuento;

    @BeforeEach
    public void setEnv() {
        IVA = 21.0;
        descuento = 20.0;
        cantidadParaDescuento = 15f;
    }

    @Test
    public void givenOrdenCompra_whenRun_thenFacturacionIsSuccess() {
        //Arrange
        Long idCliente = 14L;

        // prod sin dto x cantidad
        int cantProd1 = 1;
        double precioProd1 = 3.;

        // // prod con dto x cantidad
        int cantProd2 = (int) (cantidadParaDescuento + 1);
        double precioProd2 = 6.;

        List<ProductoCantidadDTO> prodCantList = List.of(
                new ProductoCantidadDTO(1L, cantProd1),
                new ProductoCantidadDTO(2L, cantProd2)
        );

        OrdenCompraDTO ordenCompra = new OrdenCompraDTO();
        ordenCompra.setProductoCantidadList(prodCantList);

        Factura f1 = new Factura();
        LocalDate now = LocalDate.now();

        long idSolicitud1 = 1L;

        double precio1 = cantProd1 * precioProd1;
        double precio2 = cantProd2 * precioProd2;
        precio2 = precio2 - (precio2 * descuento / 100.);

        double precioSinIva = precio1 + precio2;

        double precioConIva1 = (precio1 + IVA) / 100.;
        double precioConIva2 = (precio2 + IVA) / 100.;
        double precioConIva = precioConIva1 + precioConIva2;

        DetalleFactura df1 = new DetalleFactura().id(10L).idProducto(1L).cantidad((float) cantProd1);
        DetalleFactura df2 = new DetalleFactura().id(11L).idProducto(2L).cantidad((float) cantProd2);

        //  Mocks
        Mockito.when(facturaRepository.save(Mockito.any(Factura.class)))
                .thenReturn(Mono.just(f1
                        .iva(IVA)
                        .idCliente(idCliente)
                        .fecha(now)
                        .id(1L)))
                .thenReturn(Mono.just(f1
                        .totalSinIva(precioSinIva)
                        .totalConIva(precioConIva)));

        Mockito.when(facturadorHttpClient.checkAllStock(ordenCompra))
                .thenReturn(idSolicitud1);

        Mockito.when(facturadorHttpClient.decreaseAllStock(idSolicitud1))
                .thenReturn(Mono.just(""));

        Mockito.when(facturadorHttpClient.getPrices(ordenCompra.getListProductsOnly().toString()))
                .thenReturn(new JSONObject().put("1", precioProd1).put("2", precioProd2));

        Mockito.when(detalleFacRepository.save(any(DetalleFactura.class)))
                .thenReturn(Mono.just(df1))
                .thenReturn(Mono.just(df2));

        //Act
        Factura facturaCreada = facturadorService.run(idCliente, ordenCompra);

        //Assert
        Mockito.verify(facturaRepository, Mockito.times(2)).save(Mockito.any(Factura.class));
        Mockito.verify(detalleFacRepository, Mockito.times(2)).save(Mockito.any(DetalleFactura.class));

        Mockito.verify(facturadorHttpClient, Mockito.times(1)).checkAllStock(ordenCompra);
        Mockito.verify(facturadorHttpClient, Mockito.times(1)).decreaseAllStock(idSolicitud1);
        Mockito.verify(facturadorHttpClient, Mockito.times(1)).getPrices(anyString());

        // Check factura!
        Assertions.assertEquals(f1.getId(), facturaCreada.getId());
        Assertions.assertEquals(f1.getDetalleFacturas(), facturaCreada.getDetalleFacturas());
        Assertions.assertEquals(f1.getTotalSinIva(), facturaCreada.getTotalSinIva());
        Assertions.assertEquals(f1.getTotalConIva(), facturaCreada.getTotalConIva());

    }

}
