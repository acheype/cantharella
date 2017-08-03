/*
 * #%L
 * Cantharella :: Web
 * $Id: ManageErreurTestBioPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/domain/config/ManageErreurTestBioPage.java $
 * %%
 * Copyright (C) 2009 - 2012 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package nc.ird.cantharella.web.pages.domain.config;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.ErreurTestBio;
import nc.ird.cantharella.data.validation.utils.ModelValidator;
import nc.ird.cantharella.service.services.TestBioService;
import nc.ird.cantharella.web.pages.TemplatePage;
import nc.ird.cantharella.web.utils.behaviors.JSConfirmationBehavior;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.security.AuthRole;
import nc.ird.cantharella.web.utils.security.AuthRoles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Page for adding/update/delete a new "ErreurTest"
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@AuthRoles({ AuthRole.ADMIN })
public final class ManageErreurTestBioPage extends TemplatePage {

    /** Action : create */
    private static final String ACTION_CREATE = "Create";

    /** Action : update */
    private static final String ACTION_UPDATE = "Update";

    /** Action : delete */
    public static final String ACTION_DELETE = "Delete";

    /** The return page parameter (key and value) */
    final private String[] RETURN_PARAM = { "erreurTestBio", "opened" };

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(ManageErreurTestBioPage.class);

    /** erreurTest Model */
    private final IModel<ErreurTestBio> erreurTestModel;

    /** Service : test (for erreurs) */
    @SpringBean
    private TestBioService testService;

    /** Model validateur */
    @SpringBean(name = "webModelValidator")
    private ModelValidator validator;

    /**
     * Constructor
     */
    public ManageErreurTestBioPage() {
        this(null);
    }

    /**
     * Constructor. If idErreurTest is null, creating a new error, else editing the corresponding error
     * 
     * @param idErreurTest The ID of the erreurTest
     */
    public ManageErreurTestBioPage(Integer idErreurTest) {
        super(ManageErreurTestBioPage.class);

        boolean createMode;

        // model initialization
        if (idErreurTest == null) {
            // creation mode
            erreurTestModel = new Model<ErreurTestBio>(new ErreurTestBio());
            createMode = true;
        } else {
            try {
                erreurTestModel = new Model<ErreurTestBio>(testService.loadErreurTestBio(idErreurTest));
            } catch (DataNotFoundException e) {
                LOG.error(e.getMessage(), e);
                throw new UnexpectedException(e);
            }
            createMode = false;
        }

        // bind with markup
        final Form<Void> formView = new Form<Void>("Form");

        formView.add(new TextField<String>("ErreurTestBio.nom", new PropertyModel<String>(erreurTestModel, "nom")));
        formView.add(new TextArea<String>("ErreurTestBio.description", new PropertyModel<String>(erreurTestModel,
                "description")));

        // Action : create the erreurTest
        Button createButton = new SubmittableButton(ACTION_CREATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                testService.createErreurTest(erreurTestModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_CREATE);
                PageParameters params = new PageParameters();
                params.add(RETURN_PARAM[0], RETURN_PARAM[1]);
                setResponsePage(ListConfigurationPage.class, params);
            }

            @Override
            public void onValidate() {
                validateModel();
            }
        });
        createButton.setVisibilityAllowed(createMode);
        formView.add(createButton);

        // Action : update the erreurTest
        Button updateButton = new SubmittableButton(ACTION_UPDATE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                testService.updateErreurTestBio(erreurTestModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_UPDATE);
                PageParameters params = new PageParameters();
                params.add(RETURN_PARAM[0], RETURN_PARAM[1]);
                setResponsePage(ListConfigurationPage.class, params);
            }

            @Override
            public void onValidate() {
                validateModel();
            }
        });
        updateButton.setVisibilityAllowed(!createMode);
        formView.add(updateButton);

        // Action : suppression
        Button deleteButton = new SubmittableButton(ACTION_DELETE, new SubmittableButtonEvents() {
            @Override
            public void onProcess() throws DataConstraintException {
                testService.deleteErreurTest(erreurTestModel.getObject());
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_DELETE);
                PageParameters params = new PageParameters();
                params.add(RETURN_PARAM[0], RETURN_PARAM[1]);
                setResponsePage(ListConfigurationPage.class, params);
            }
        });
        deleteButton.setVisibilityAllowed(!createMode);
        deleteButton.setDefaultFormProcessing(false);
        deleteButton.add(new JSConfirmationBehavior(getStringModel("Confirm")));
        formView.add(deleteButton);

        formView.add(new Link<Void>("Cancel") {
            @Override
            public void onClick() {
                PageParameters params = new PageParameters();
                params.add(RETURN_PARAM[0], RETURN_PARAM[1]);
                setResponsePage(ListConfigurationPage.class, params);
            }
        });

        add(formView);
    }

    /**
     * Validate model
     */
    private void validateModel() {
        addValidationErrors(validator.validate(erreurTestModel.getObject(), getSession().getLocale()));
    }
}
