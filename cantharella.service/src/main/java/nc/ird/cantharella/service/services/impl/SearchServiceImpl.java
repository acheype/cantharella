/*
 * #%L
 * Cantharella :: Service
 * $Id: SearchServiceImpl.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/impl/SearchServiceImpl.java $
 * %%
 * Copyright (C) 2009 - 2013 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
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
package nc.ird.cantharella.service.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import nc.ird.cantharella.data.exceptions.UnexpectedException;
import nc.ird.cantharella.data.model.Campagne;
import nc.ird.cantharella.data.model.Extraction;
import nc.ird.cantharella.data.model.Lot;
import nc.ird.cantharella.data.model.Molecule;
import nc.ird.cantharella.data.model.Purification;
import nc.ird.cantharella.data.model.ResultatTestBio;
import nc.ird.cantharella.data.model.ResultatTestBio.TypeResultat;
import nc.ird.cantharella.data.model.Specimen;
import nc.ird.cantharella.data.model.Station;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.service.model.SearchBean;
import nc.ird.cantharella.service.model.SearchResult;
import nc.ird.cantharella.service.services.LotService;
import nc.ird.cantharella.service.services.SearchService;
import nc.ird.cantharella.service.services.SpecimenService;
import nc.ird.cantharella.service.services.StationService;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.ReaderUtil;
import org.apache.lucene.util.Version;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.MassIndexer;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implémentation du service de recherche.
 * 
 * @author echatellier
 */
@Service
public class SearchServiceImpl implements SearchService {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(SearchServiceImpl.class);

    /** Hibernate session factory. */
    @Resource
    private SessionFactory sessionFactory;

    /** Lot service for permissions **/
    @Autowired
    private LotService lotService;

    /** Station service for permissions **/
    @Autowired
    private StationService stationService;

    /** Specimen service for permission **/
    @Autowired
    private SpecimenService specimenService;

    /** {@inheritDoc} */
    @Override
    public void reIndex() {
        long before = System.currentTimeMillis();
        if (LOG.isInfoEnabled()) {
            LOG.info("Starting full rebuild on lucene index");
        }
        // get hibernate search session
        Session session = sessionFactory.getCurrentSession();
        FullTextSession fullTextSession = Search.getFullTextSession(session);
        try {
            MassIndexer indexer = fullTextSession.createIndexer();
            indexer.batchSizeToLoadObjects(1);
            indexer.threadsToLoadObjects(1);
            indexer.threadsForSubsequentFetching(1);
            indexer.startAndWait();

            if (LOG.isInfoEnabled()) {
                long after = System.currentTimeMillis();
                LOG.info("Lucene index rebuilded in " + (after - before) + " ms");
            }
        } catch (InterruptedException ex) {
            throw new UnexpectedException("Can't rebuild index", ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public SearchResult search(SearchBean search, Utilisateur utilisateur) {
        SearchResult result = new SearchResult();

        // get hibernate search session
        Session session = sessionFactory.getCurrentSession();
        FullTextSession fullTextSession = Search.getFullTextSession(session);

        // le flushToIndexes n'est pas correct ici, il sert juste a faire
        // passer les tests pour que les indexes lucene contiennent des données
        // nécéssaire car il ne sont écrits que lorsque Spring commmit la
        // transaction (c'est à dire jamais dans les tests)
        // fullTextSession.flushToIndexes();
        // fullTextSession.setFlushMode(FlushMode.MANUAL);
        // fullTextSession.setCacheMode(CacheMode.IGNORE);

        try {

            // get lucene query
            String strQuery = "";
            if (search.getQuery() != null) {
                strQuery += search.getQuery();
            }
            strQuery += " ";
            if (search.getCountry() != null) {
                strQuery += search.getCountry();
            }
            strQuery = strQuery.trim();

            // default init to empty list if query is null or empty
            List<Specimen> specimens = Collections.EMPTY_LIST;
            List<Lot> lots = Collections.EMPTY_LIST;
            List<Extraction> extractions = Collections.EMPTY_LIST;
            List<Purification> purifications = Collections.EMPTY_LIST;
            List<ResultatTestBio> resultatTestBios = Collections.EMPTY_LIST;
            List<Station> resultatStations = Collections.EMPTY_LIST;
            List<Molecule> resultatMolecules = Collections.EMPTY_LIST;

            if (!strQuery.isEmpty()) {
                // wrap Lucene query in a org.hibernate.Query
                org.hibernate.Query hibSpecimen = getQuery(fullTextSession, Specimen.class, strQuery, utilisateur);
                org.hibernate.Query hibLot = getQuery(fullTextSession, Lot.class, strQuery, utilisateur);
                org.hibernate.Query hibExtraction = getQuery(fullTextSession, Extraction.class, strQuery, utilisateur);
                org.hibernate.Query hibPurification = getQuery(fullTextSession, Purification.class, strQuery,
                        utilisateur);
                org.hibernate.Query hibResultatTestBio = getQuery(fullTextSession, ResultatTestBio.class, strQuery,
                        utilisateur);
                org.hibernate.Query hibStation = getQuery(fullTextSession, Station.class, strQuery, utilisateur);
                org.hibernate.Query hibMolecule = getQuery(fullTextSession, Molecule.class, strQuery, utilisateur);

                // perform search
                specimens = hibSpecimen.list();
                lots = hibLot.list();
                extractions = hibExtraction.list();
                purifications = hibPurification.list();
                resultatTestBios = hibResultatTestBio.list();
                resultatStations = hibStation.list();
                resultatMolecules = hibMolecule.list();
            }

            result.setSpecimens(specimens);
            result.setLots(lots);
            result.setExtractions(extractions);
            result.setPurifications(purifications);
            result.setResultatTestBios(resultatTestBios);
            result.setStations(resultatStations);
            result.setMolecules(resultatMolecules);

            // security manually managed
            result = filterResults(result, utilisateur);

        } catch (ParseException ex) {
            throw new UnexpectedException("Can't parse query", ex);
        }

        // tx.commit();
        // session.close();
        return result;
    }

    /**
     * Prepare hibernate query with lucene query implementation to search for query string on specified single type.
     * 
     * @param fullTextSession search session
     * @param clazz type
     * @param strQuery query string
     * @param utilisateur user
     * @return hibernate query implemented by lucene query
     * @throws ParseException
     */
    protected org.hibernate.Query getQuery(FullTextSession fullTextSession, Class<?> clazz, String strQuery,
            Utilisateur utilisateur) throws ParseException {

        // get search factory
        SearchFactory searchFactory = fullTextSession.getSearchFactory();

        // build a multi field query parser to search in all fields
        IndexReader reader = searchFactory.getIndexReaderAccessor().open(clazz);
        FieldInfos fieldInfos = ReaderUtil.getMergedFieldInfos(reader);
        List<String> fieldList = new ArrayList<>();
        for (int i = 0; i < fieldInfos.size(); i++) {
            String fieldName = fieldInfos.fieldName(i);
            // il semble impossible de ne pas recuperer l'id du document
            // on l'exclut donc manuellement
            if (!fieldName.startsWith("id") && !fieldName.contains(".id") /*
                                                                           * &&
                                                                           * !
                                                                           * fieldName
                                                                           * .
                                                                           * endsWith
                                                                           * (
                                                                           * ".pk2"
                                                                           * )
                                                                           */) {
                fieldList.add(fieldName);
            }
        }
        Analyzer analyzer = searchFactory.getAnalyzer(clazz);
        QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_36,
                fieldList.toArray(new String[fieldList.size()]), analyzer);
        searchFactory.getIndexReaderAccessor().close(reader);

        // autorisation de "*" en premier caractere
        parser.setAllowLeadingWildcard(true);
        // change default operator to AND
        parser.setDefaultOperator(Operator.AND);
        // create lucene query
        Query query = parser.parse(strQuery);

        // convert lucene query to hibernate query
        FullTextQuery hibQuery = fullTextSession.createFullTextQuery(query, clazz);

        return hibQuery;
    }

    /**
     * Iterate over all results and remove those that user is not allowed to see.
     * 
     * @see Utilisateur#getLotsDroits()
     * @see Utilisateur#getCampagnesDroits()
     */
    private SearchResult filterResults(SearchResult result, Utilisateur utilisateur) {

        // get campagnes and lots data to manage rigths
        List<Campagne> campagnes = new ArrayList<Campagne>();
        campagnes.addAll(utilisateur.getCampagnesCreees());
        campagnes.addAll(utilisateur.getCampagnesDroits().keySet());
        List<Lot> lots = new ArrayList<Lot>();
        lots.addAll(utilisateur.getLotsCrees());
        lots.addAll(utilisateur.getLotsDroits().keySet());

        // SearchResult#lots
        Iterator<Lot> itLots = result.getLots().iterator();
        while (itLots.hasNext()) {
            Lot lot = itLots.next();
            if (!lotService.isLotAccessibleByUser(lot, utilisateur)) {
                itLots.remove();
            }
        }

        // SearchResult#extractions
        Iterator<Extraction> itExtractions = result.getExtractions().iterator();
        while (itExtractions.hasNext()) {
            Extraction extraction = itExtractions.next();
            Lot lot = extraction.getLot();
            if (!lotService.isLotAccessibleByUser(lot, utilisateur)) {
                itExtractions.remove();
            }
        }

        // SearchResult#purifications
        Iterator<Purification> itPurifications = result.getPurifications().iterator();
        while (itPurifications.hasNext()) {
            Purification purification = itPurifications.next();
            Lot lot = purification.getLotSource();
            if (!lotService.isLotAccessibleByUser(lot, utilisateur)) {
                itPurifications.remove();
            }
        }

        // SearchResult#resultatTestBios
        Iterator<ResultatTestBio> itResultatTestBios = result.getResultatTestBios().iterator();
        while (itResultatTestBios.hasNext()) {
            ResultatTestBio resultatTestBio = itResultatTestBios.next();
            Lot lot = resultatTestBio.getLotSource();
            // le lot peut être null pour les tests temoin
            if (resultatTestBio.getTypeResultat() != TypeResultat.PRODUIT
                    || !lotService.isLotAccessibleByUser(lot, utilisateur)) {
                itResultatTestBios.remove();
            }
        }

        // SearchResult#stations
        Set<Station> userStations = stationService.listStationsForUser(utilisateur);
        result.getStations().retainAll(userStations);

        // SearchResult#specimens
        Set<Specimen> userSpecimens = specimenService.listSpecimensForUser(utilisateur);
        result.getSpecimens().retainAll(userSpecimens);

        // SearchResult#molecules : all visible, no filtering

        return result;
    }
}
