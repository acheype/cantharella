package nc.ird.cantharella.data.model.search;

/*
 * #%L
 * Cantharella :: Data
 * $Id: ProduitBridge.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.data/src/main/java/nc/ird/cantharella/data/model/search/ProduitBridge.java $
 * %%
 * Copyright (C) 2012 - 2013 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
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

import nc.ird.cantharella.data.model.Extrait;
import nc.ird.cantharella.data.model.Fraction;
import nc.ird.cantharella.data.model.Lot;
import nc.ird.cantharella.data.model.Produit;
import nc.ird.cantharella.data.model.ResultatTestBio;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ResultatTestBio} class contains a polymorphic entity that can't be properly indexed by hibernate search.
 * 
 * @author Eric Chatellier
 */
public class ProduitBridge implements FieldBridge {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(ProduitBridge.class);

    /** {@inheritDoc} */
    @Override
    public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {

        Produit produit = (Produit) value;
        if (LOG.isTraceEnabled()) {
            LOG.trace("Custom indexing of Produit entity : " + produit);
        }

        // commons information for all produit
        Lot lot = null;
        if (produit instanceof Extrait) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Custom indexing of Extrait entity");
            }
            Extrait extrait = (Extrait) produit;
            lot = extrait.getExtraction().getLot();
        } else if (produit instanceof Fraction) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Custom indexing of Fraction entity");
            }
            Fraction fraction = (Fraction) produit;
            lot = fraction.getPurification().getLotSource();
        }

        // ref field tokenized with a non fr analyzer
        // to avoid duplicated letter removal (00)
        document.add(new Field(name + ".lot.ref", lot.getRef(), luceneOptions.getStore(), luceneOptions.getIndex(),
                luceneOptions.getTermVector()));

        if (StringUtils.isNotBlank(lot.getSpecimenRef().getEmbranchement())) {
            document.add(new Field(name + ".lot.specimen.embranchement", lot.getSpecimenRef().getEmbranchement(),
                    luceneOptions.getStore(), luceneOptions.getIndex(), luceneOptions.getTermVector()));
        }
        if (StringUtils.isNotBlank(lot.getSpecimenRef().getFamille())) {
            document.add(new Field(name + ".lot.specimen.famille", lot.getSpecimenRef().getFamille(), luceneOptions
                    .getStore(), luceneOptions.getIndex(), luceneOptions.getTermVector()));
        }
        if (StringUtils.isNotBlank(lot.getSpecimenRef().getGenre())) {
            document.add(new Field(name + ".lot.specimen.genre", lot.getSpecimenRef().getGenre(), luceneOptions
                    .getStore(), luceneOptions.getIndex(), luceneOptions.getTermVector()));
        }
        if (StringUtils.isNotBlank(lot.getSpecimenRef().getEspece())) {
            document.add(new Field(name + ".lot.specimen.espece", lot.getSpecimenRef().getEspece(), luceneOptions
                    .getStore(), luceneOptions.getIndex(), luceneOptions.getTermVector()));
        }
        document.add(new Field(name + ".lot.campagne.nom", lot.getCampagne().getNom(), luceneOptions.getStore(),
                luceneOptions.getIndex(), luceneOptions.getTermVector()));
        document.add(new Field(name + ".lot.campagne.codePays", lot.getCampagne().getCodePays(), luceneOptions
                .getStore(), luceneOptions.getIndex(), luceneOptions.getTermVector()));

        /* commons information for all produit
        List<LotPersonneDroits> personnesDroits = lot.getPersonnesDroits();
        for (LotPersonneDroits lotPersonneDroit : personnesDroits) {
            document.add(new Field("droit.pk2", String.valueOf(lotPersonneDroit.getId().getPk2().getIdPersonne()),
                    luceneOptions.getStore(), luceneOptions.getIndex(), luceneOptions.getTermVector()));
        }
        List<CampagnePersonneDroits> campagnesDroits = lot.getCampagne().getPersonnesDroits();
        for (CampagnePersonneDroits campagnesDroit : campagnesDroits) {
            document.add(new Field("droit.pk2", String.valueOf(campagnesDroit.getId().getPk2().getIdPersonne()),
                    luceneOptions.getStore(), luceneOptions.getIndex(), luceneOptions.getTermVector()));
        }*/
    }
}
