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

describe('DetalleFactura e2e test', () => {
  const detalleFacturaPageUrl = '/detalle-factura';
  const detalleFacturaPageUrlPattern = new RegExp('/detalle-factura(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const detalleFacturaSample = { cantidad: 86143 };

  let detalleFactura;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/detalle-facturas+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/detalle-facturas').as('postEntityRequest');
    cy.intercept('DELETE', '/api/detalle-facturas/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (detalleFactura) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/detalle-facturas/${detalleFactura.id}`,
      }).then(() => {
        detalleFactura = undefined;
      });
    }
  });

  it('DetalleFacturas menu should load DetalleFacturas page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('detalle-factura');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('DetalleFactura').should('exist');
    cy.url().should('match', detalleFacturaPageUrlPattern);
  });

  describe('DetalleFactura page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(detalleFacturaPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create DetalleFactura page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/detalle-factura/new$'));
        cy.getEntityCreateUpdateHeading('DetalleFactura');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', detalleFacturaPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/detalle-facturas',
          body: detalleFacturaSample,
        }).then(({ body }) => {
          detalleFactura = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/detalle-facturas+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [detalleFactura],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(detalleFacturaPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details DetalleFactura page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('detalleFactura');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', detalleFacturaPageUrlPattern);
      });

      it('edit button click should load edit DetalleFactura page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('DetalleFactura');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', detalleFacturaPageUrlPattern);
      });

      it('edit button click should load edit DetalleFactura page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('DetalleFactura');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', detalleFacturaPageUrlPattern);
      });

      it('last delete button click should delete instance of DetalleFactura', () => {
        cy.intercept('GET', '/api/detalle-facturas/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('detalleFactura').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', detalleFacturaPageUrlPattern);

        detalleFactura = undefined;
      });
    });
  });

  describe('new DetalleFactura page', () => {
    beforeEach(() => {
      cy.visit(`${detalleFacturaPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('DetalleFactura');
    });

    it('should create an instance of DetalleFactura', () => {
      cy.get(`[data-cy="cantidad"]`).type('66665').should('have.value', '66665');

      cy.get(`[data-cy="idProducto"]`).type('12035').should('have.value', '12035');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        detalleFactura = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', detalleFacturaPageUrlPattern);
    });
  });
});
