/*
 * #%L
 * Cantharella :: Web
 * $Id: ContactPage.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.web/src/main/java/nc/ird/cantharella/web/pages/ContactPage.java $
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
package nc.ird.cantharella.web.pages;

import nc.ird.cantharella.data.validation.utils.ModelValidator;
import nc.ird.cantharella.service.exceptions.EmailException;
import nc.ird.cantharella.service.services.PersonneService;
import nc.ird.cantharella.web.pages.model.CaptchaModel;
import nc.ird.cantharella.web.pages.model.ContactModel;
import nc.ird.cantharella.web.utils.forms.SubmittableButton;
import nc.ird.cantharella.web.utils.forms.SubmittableButtonEvents;
import nc.ird.cantharella.web.utils.security.AuthRole;
import nc.ird.cantharella.utils.CaptchaTools;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.markup.html.captcha.CaptchaImageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Contact page
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
public class ContactPage extends TemplatePage {

    /** Action : send */
    private static final String ACTION_SEND = "Send";

    /** Service : personne */
    @SpringBean
    private PersonneService personneService;

    /** Validateur modèle */
    @SpringBean(name = "webModelValidator")
    private ModelValidator validator;

    /**
     * Constructeur
     */
    public ContactPage() {
        super(ContactPage.class);

        // AuthMode = the user is authenticated, we already know his e-mail, the form is not protected with a captcha
        final boolean authMode = getSession().getRole() != AuthRole.VISITOR;

        // Models initialization
        final IModel<ContactModel> contactModel = new Model<ContactModel>(new ContactModel());
        final IModel<CaptchaModel> captchaModel = new Model<CaptchaModel>(new CaptchaModel());
        if (!authMode) {
            captchaModel.getObject().setCaptchaTextGenerated(CaptchaTools.random());
        }

        final Form<Void> formView = new Form<Void>("Form");

        // ContactModel fields
        formView.add(new TextField<String>("ContactModel.mail", authMode ? new Model<String>(getSession()
                .getUtilisateur().getCourriel()) : new PropertyModel<String>(contactModel, "mail"))
                .setEnabled(!authMode));
        formView.add(new TextField<String>("ContactModel.subject", new PropertyModel<String>(contactModel, "subject")));
        formView.add(new TextArea<String>("ContactModel.message", new PropertyModel<String>(contactModel, "message")));

        // CaptchaModel fields
        MarkupContainer captcha = new WebMarkupContainer("captcha");
        captcha.setVisibilityAllowed(!authMode);
        captcha.add(new NonCachingImage("CaptchaModel.captchaImage", new CaptchaImageResource(captchaModel.getObject()
                .getCaptchaTextGenerated())));
        final Component captchaTextView = new TextField<String>("CaptchaModel.captchaText", new PropertyModel<String>(
                captchaModel, "captchaText"));
        captcha.add(captchaTextView);
        formView.add(captcha);

        // Action button
        formView.add(new SubmittableButton(ACTION_SEND, new SubmittableButtonEvents() {

            @Override
            public void onProcess() throws EmailException {
                personneService.sendMailAdmins(contactModel.getObject().getSubject(), contactModel.getObject()
                        .getMessage(), contactModel.getObject().getMail());
            }

            @Override
            public void onSuccess() {
                successNextPage(ACTION_SEND);

                setResponsePage(getApplication().getHomePage());
            }

            @Override
            public void onValidate() {
                // Enforce the sender e-mail in authMode
                if (authMode) {
                    contactModel.getObject().setMail(getSession().getUtilisateur().getCourriel());
                }

                // Validate ContactModel
                addValidationErrors(validator.validate(contactModel.getObject(), getSession().getLocale()));

                // Validate CaptchaModel
                if (!authMode && !captchaModel.getObject().validate()) {
                    errorCurrentPage(captchaTextView);
                }
            }

            /**
             * Reset the captcha, on each page reload
             */
            /**
             * private void resetCaptcha() { if (!authMode) { captchaModel.getObject().setCaptchaText(null);
             * captchaModel.getObject().setCaptchaTextGenerated(CaptchaTools.random()); formView.replace(new
             * NonCachingImage("CaptchaModel.captchaImage", new CaptchaImageResource(
             * captchaModel.getObject().getCaptchaTextGenerated()))); } }
             **/
        }));

        add(formView);
    }
}
