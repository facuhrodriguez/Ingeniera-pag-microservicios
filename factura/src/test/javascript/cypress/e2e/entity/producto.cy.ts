import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Producto e2e test', () => {
  const productoPageUrl = '/factura/producto';
  const productoPageUrlPattern = new RegExp('/factura/producto(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const productoSample = { marca: 'transmitting' };

  let producto;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/factura/api/productos+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/factura/api/productos').as('postEntityRequest');
    cy.intercept('DELETE', '/services/factura/api/productos/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (producto) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/factura/api/productos/${producto.id}`,
      }).then(() => {
        producto = undefined;
      });
    }
  });

  it('Productos menu should load Productos page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('factura/producto');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Producto').should('exist');
    cy.url().should('match', productoPageUrlPattern);
  });

  describe('Producto page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(productoPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Producto page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/factura/producto/new$'));
        cy.getEntityCreateUpdateHeading('Producto');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productoPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/factura/api/productos',
          body: productoSample,
        }).then(({ body }) => {
          producto = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/factura/api/productos+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [producto],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(productoPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Producto page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('producto');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productoPageUrlPattern);
      });

      it('edit button click should load edit Producto page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Producto');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productoPageUrlPattern);
      });

      it('edit button click should load edit Producto page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Producto');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productoPageUrlPattern);
      });

      it('last delete button click should delete instance of Producto', () => {
        cy.intercept('GET', '/services/factura/api/productos/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('producto').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productoPageUrlPattern);

        producto = undefined;
      });
    });
  });

  describe('new Producto page', () => {
    beforeEach(() => {
      cy.visit(`${productoPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Producto');
    });

    it('should create an instance of Producto', () => {
      cy.get(`[data-cy="marca"]`).type('Isle Cambridgeshire Steel').should('have.value', 'Isle Cambridgeshire Steel');

      cy.get(`[data-cy="nombre"]`).type('ADP').should('have.value', 'ADP');

      cy.get(`[data-cy="descripcion"]`).type('Manager Won').should('have.value', 'Manager Won');

      cy.get(`[data-cy="precio"]`).type('61134').should('have.value', '61134');

      cy.get(`[data-cy="stock"]`).type('82570').should('have.value', '82570');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        producto = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', productoPageUrlPattern);
    });
  });
});
