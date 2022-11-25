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

describe('Factura e2e test', () => {
  const facturaPageUrl = '/factura';
  const facturaPageUrlPattern = new RegExp('/factura(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const facturaSample = { fecha: '2022-11-24' };

  let factura;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/facturas+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/facturas').as('postEntityRequest');
    cy.intercept('DELETE', '/api/facturas/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (factura) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/facturas/${factura.id}`,
      }).then(() => {
        factura = undefined;
      });
    }
  });

  it('Facturas menu should load Facturas page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('factura');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Factura').should('exist');
    cy.url().should('match', facturaPageUrlPattern);
  });

  describe('Factura page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(facturaPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Factura page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/factura/new$'));
        cy.getEntityCreateUpdateHeading('Factura');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', facturaPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/facturas',
          body: facturaSample,
        }).then(({ body }) => {
          factura = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/facturas+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [factura],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(facturaPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Factura page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('factura');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', facturaPageUrlPattern);
      });

      it('edit button click should load edit Factura page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Factura');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', facturaPageUrlPattern);
      });

      it('edit button click should load edit Factura page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Factura');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', facturaPageUrlPattern);
      });

      it('last delete button click should delete instance of Factura', () => {
        cy.intercept('GET', '/api/facturas/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('factura').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', facturaPageUrlPattern);

        factura = undefined;
      });
    });
  });

  describe('new Factura page', () => {
    beforeEach(() => {
      cy.visit(`${facturaPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Factura');
    });

    it('should create an instance of Factura', () => {
      cy.get(`[data-cy="fecha"]`).type('2022-11-24').blur().should('have.value', '2022-11-24');

      cy.get(`[data-cy="totalSinIva"]`).type('16907').should('have.value', '16907');

      cy.get(`[data-cy="iva"]`).type('96199').should('have.value', '96199');

      cy.get(`[data-cy="totalConIva"]`).type('45579').should('have.value', '45579');

      cy.get(`[data-cy="idCliente"]`).type('84264').should('have.value', '84264');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        factura = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', facturaPageUrlPattern);
    });
  });
});
