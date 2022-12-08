package com.ingenieria.producto.service;

import com.ingenieria.producto.domain.Producto;
import com.ingenieria.producto.repository.ProductoRepository;
import com.ingenieria.producto.service.dto.getprecio.ProductoListDTO;
import com.ingenieria.producto.service.dto.getprecio.ProductoPrecioDTO;
import com.ingenieria.producto.service.dto.ordencompra.OrdenCompraDTO;
import com.ingenieria.producto.service.dto.ordencompra.ProductoCantidadDTO;
import com.ingenieria.producto.service.errors.ProductoNoRegistradoException;
import com.ingenieria.producto.service.errors.StockInsuficienteException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    public static Producto p1, p2, p3;

    @BeforeEach
    public void init() {
        p1 = new Producto()
                .id(1L)
                .nombre("Coca-Cola 600ml")
                .descripcion("Gaseosa")
                .marca("Coca-Cola Company")
                .precio(10.f)
                .stock(100);

        p2 = new Producto()
                .id(2L)
                .nombre("Fanta Light 600ml")
                .descripcion("Gaseosa")
                .marca("Coca-Cola Company")
                .precio(15.f)
                .stock(100);

        p3 = new Producto()
                .id(3L)
                .nombre("Pepsi 500ml")
                .descripcion("Gaseosa")
                .marca("Pepsico")
                .precio(12.f)
                .stock(3);
    }

    @Test
    public void givenOrdenCompra_whenCheckAllStock_thenReturnSuccess() {
        // Arrange
        int cantProd1 = 4;
        int cantProd2 = 5;

        List<ProductoCantidadDTO> prodCantList = List.of(
                new ProductoCantidadDTO(1L, cantProd1),
                new ProductoCantidadDTO(2L, cantProd2)
        );

        OrdenCompraDTO ordenCompra = new OrdenCompraDTO();
        ordenCompra.setProductoCantidadList(prodCantList);

        // Mocks
        Mockito.when(productoRepository.findById(anyLong()))
                .thenReturn(Mono.just(p1))
                .thenReturn(Mono.just(p2));

        // Act
        Mono<Long> result = productoService.checkAllStock(ordenCompra);

        // Assert
        Long idSolicitud = result.block();
        Assertions.assertNotEquals(null, idSolicitud);
        Mockito.verify(productoRepository, Mockito.times(2)).findById(anyLong());

    }

    @Test
    public void givenANotExistingProductInOrden_whenCheckAllStock_throwProductoNoRegistradoException() {
        // Arrange
        int cantProd1 = 1;

        List<ProductoCantidadDTO> prodCantList = List.of(
                new ProductoCantidadDTO(12424235L, cantProd1)
        );

        OrdenCompraDTO ordenCompra = new OrdenCompraDTO();
        ordenCompra.setProductoCantidadList(prodCantList);

        // Mocks
        Mockito.when(productoRepository.findById(anyLong()))
                .thenReturn(Mono.empty());
        //Act
        Throwable thrown = catchThrowable(() -> productoService.checkAllStock(ordenCompra).block());

        //Assert
        assertThat(thrown).isInstanceOf(ProductoNoRegistradoException.class);

    }

    @Test
    public void givenAOverflowStock_whenCheckAllStock_throwStockInsuficienteException() {
        // Arrange
        int cantProd1 = p1.getStock() + 1;
        int cantProd2 = 10;

        List<ProductoCantidadDTO> prodCantList = List.of(
                new ProductoCantidadDTO(p1.getId(), cantProd1),
                new ProductoCantidadDTO(p2.getId(), cantProd2)
        );

        OrdenCompraDTO ordenCompra = new OrdenCompraDTO();
        ordenCompra.setProductoCantidadList(prodCantList);

        // Mocks
        Mockito.when(productoRepository.findById(anyLong()))
                .thenReturn(Mono.just(p1))
                .thenReturn(Mono.just(p2));
        //Act
        Throwable thrown = catchThrowable(() -> productoService.checkAllStock(ordenCompra).block());

        //Assert
        assertThat(thrown).isInstanceOf(StockInsuficienteException.class);

    }

    @Test
    public void givenAOrdenCompra_whenDecrementarStock_thenReturnSuccessfully() {
        //Arrange

        int cantProd1 = 5;
        int cantProd2 = 1;

        List<ProductoCantidadDTO> prodCantList = List.of(
                new ProductoCantidadDTO(p1.getId(), cantProd1),
                new ProductoCantidadDTO(p2.getId(), cantProd2)
        );

        OrdenCompraDTO ordenCompra = new OrdenCompraDTO();
        ordenCompra.setProductoCantidadList(prodCantList);

        //Mocks
        Mockito.when(productoRepository.findById(anyLong()))
                .thenReturn(Mono.just(p1))
                .thenReturn(Mono.just(p2))
                .thenReturn(Mono.just(p1))
                .thenReturn(Mono.just(p2));

        Mockito.when(productoRepository.save(any()))
                .thenReturn(Mono.just(p1))
                .thenReturn(Mono.just(p2));

        // solo para que ordenCompraTemp esté seteado...
        long idSolicitud = productoService.checkAllStock(ordenCompra).block();

        //Act
        productoService.decrementarStock(idSolicitud).block();

        //Assert
        Mockito.verify(productoRepository, Mockito.times(4)).findById(anyLong());

        int expectedValueStock1 = 95;
        int expectedValueStock2 = 99;
        Assertions.assertEquals(expectedValueStock1, p1.getStock());
        Assertions.assertEquals(expectedValueStock2, p2.getStock());

    }

    @Test
    public void givenProductosList_whenGetPrecios_thenReturnSuccess() {
        //Arrange
        ProductoListDTO productos = new ProductoListDTO();
        productos.setProductoList(List.of(1L, 2L, 3L));

        //Mocks
        Mockito.when(productoRepository.findById(anyLong()))
                .thenReturn(Mono.just(p1))
                .thenReturn(Mono.just(p2))
                .thenReturn(Mono.just(p3));

        //Act
        List<ProductoPrecioDTO> result = productoService.getPrecios(productos).block();
        assert result != null;

        //Assert
        Mockito.verify(productoRepository, Mockito.times(3)).findById(anyLong());

        ProductoPrecioDTO productoPrecio1 = new ProductoPrecioDTO(1L, 10.f);
        ProductoPrecioDTO productoPrecio2 = new ProductoPrecioDTO(2L, 15.f);
        ProductoPrecioDTO productoPrecio3 = new ProductoPrecioDTO(3L, 12.f);
        List<ProductoPrecioDTO> expectedResult = List.of(productoPrecio1, productoPrecio2, productoPrecio3);

        Assertions.assertTrue(result.containsAll(expectedResult));

    }

}
