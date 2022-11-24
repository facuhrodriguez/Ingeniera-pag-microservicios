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

describe('Cliente e2e test', () => {
  const clientePageUrl = '/cliente';
  const clientePageUrlPattern = new RegExp('/cliente(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const clienteSample = { nombre: 'primary Maryland' };

  let cliente;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/clientes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/clientes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/clientes/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (cliente) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/clientes/${cliente.id}`,
      }).then(() => {
        cliente = undefined;
      });
    }
  });

  it('Clientes menu should load Clientes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('cliente');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Cliente').should('exist');
    cy.url().should('match', clientePageUrlPattern);
  });

  describe('Cliente page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(clientePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Cliente page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/cliente/new$'));
        cy.getEntityCreateUpdateHeading('Cliente');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', clientePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/clientes',
          body: clienteSample,
        }).then(({ body }) => {
          cliente = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/clientes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [cliente],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(clientePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Cliente page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('cliente');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', clientePageUrlPattern);
      });

      it('edit button click should load edit Cliente page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Cliente');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', clientePageUrlPattern);
      });

      it('edit button click should load edit Cliente page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Cliente');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', clientePageUrlPattern);
      });

      it('last delete button click should delete instance of Cliente', () => {
        cy.intercept('GET', '/api/clientes/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('cliente').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', clientePageUrlPattern);

        cliente = undefined;
      });
    });
  });

  describe('new Cliente page', () => {
    beforeEach(() => {
      cy.visit(`${clientePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Cliente');
    });

    it('should create an instance of Cliente', () => {
      cy.get(`[data-cy="nombre"]`).type('Sleek').should('have.value', 'Sleek');

      cy.get(`[data-cy="apellido"]`).type('FTP synergy haptic').should('have.value', 'FTP synergy haptic');

      cy.get(`[data-cy="direccion"]`).type('RSS').should('have.value', 'RSS');

      cy.get(`[data-cy="activo"]`).type('65809').should('have.value', '65809');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        cliente = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', clientePageUrlPattern);
    });
  });
});
