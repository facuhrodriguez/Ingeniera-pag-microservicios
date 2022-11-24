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

describe('Telefono e2e test', () => {
  const telefonoPageUrl = '/telefono';
  const telefonoPageUrlPattern = new RegExp('/telefono(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const telefonoSample = { codigoArea: 14229 };

  let telefono;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/telefonos+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/telefonos').as('postEntityRequest');
    cy.intercept('DELETE', '/api/telefonos/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (telefono) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/telefonos/${telefono.id}`,
      }).then(() => {
        telefono = undefined;
      });
    }
  });

  it('Telefonos menu should load Telefonos page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('telefono');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Telefono').should('exist');
    cy.url().should('match', telefonoPageUrlPattern);
  });

  describe('Telefono page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(telefonoPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Telefono page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/telefono/new$'));
        cy.getEntityCreateUpdateHeading('Telefono');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', telefonoPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/telefonos',
          body: telefonoSample,
        }).then(({ body }) => {
          telefono = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/telefonos+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [telefono],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(telefonoPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Telefono page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('telefono');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', telefonoPageUrlPattern);
      });

      it('edit button click should load edit Telefono page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Telefono');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', telefonoPageUrlPattern);
      });

      it('edit button click should load edit Telefono page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Telefono');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', telefonoPageUrlPattern);
      });

      it('last delete button click should delete instance of Telefono', () => {
        cy.intercept('GET', '/api/telefonos/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('telefono').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', telefonoPageUrlPattern);

        telefono = undefined;
      });
    });
  });

  describe('new Telefono page', () => {
    beforeEach(() => {
      cy.visit(`${telefonoPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Telefono');
    });

    it('should create an instance of Telefono', () => {
      cy.get(`[data-cy="codigoArea"]`).type('82232').should('have.value', '82232');

      cy.get(`[data-cy="nroTelefono"]`).type('89204').should('have.value', '89204');

      cy.get(`[data-cy="tipo"]`).type('GB Delaware world-class').should('have.value', 'GB Delaware world-class');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        telefono = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', telefonoPageUrlPattern);
    });
  });
});
