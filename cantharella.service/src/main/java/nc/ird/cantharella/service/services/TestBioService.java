/*
 * #%L
 * Cantharella :: Service
 * $Id: TestBioService.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/services/TestBioService.java $
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
package nc.ird.cantharella.service.services;

import java.util.List;
import java.util.SortedSet;

import nc.ird.cantharella.data.exceptions.DataConstraintException;
import nc.ird.cantharella.data.exceptions.DataNotFoundException;
import nc.ird.cantharella.data.model.ErreurTestBio;
import nc.ird.cantharella.data.model.MethodeTestBio;
import nc.ird.cantharella.data.model.ResultatTestBio;
import nc.ird.cantharella.data.model.TestBio;
import nc.ird.cantharella.data.model.Utilisateur;
import nc.ird.cantharella.service.utils.normalizers.ErreurTestNormalizer;
import nc.ird.cantharella.service.utils.normalizers.MethodeTestBioNormalizer;
import nc.ird.cantharella.service.utils.normalizers.TestBioNormalizer;
import nc.ird.cantharella.service.utils.normalizers.UniqueFieldNormalizer;
import nc.ird.cantharella.service.utils.normalizers.utils.Normalize;

import org.springframework.transaction.annotation.Transactional;

/**
 * Service : tests
 * 
 * @author Adrien Cheype
 */
public interface TestBioService {

    /**
     * Compte le nombre de resultats de tests biologiques
     * 
     * @return Nombre de resultats de tests biologiques
     */
    long countResultatsTestsBio();

    /**
     * Créée une manipulation de testBio
     * 
     * @param testBio La manipulation
     * @throws DataConstraintException Si la manipulation (réf) existe déjà
     */
    void createTestBio(@Normalize(TestBioNormalizer.class) TestBio testBio) throws DataConstraintException;

    /**
     * Supprime une manipulation de testBio
     * 
     * @param testBio La manipulation
     * @throws DataConstraintException En cas de données liées
     */
    void deleteTestBio(TestBio testBio) throws DataConstraintException;

    /**
     * Liste l'ensemble des résultats de tests biologiques selon les droits d'un utilisateur (triés par réf produit)
     * 
     * @param utilisateur L'utilisateur
     * @return la liste des résultats
     */
    @Transactional(readOnly = true)
    List<ResultatTestBio> listResultatsTestBio(Utilisateur utilisateur);

    /**
     * Liste l'ensemble des résultats de tests biologiques selon les droits d'un utilisateur (triés par réf produit)
     * 
     * @param utilisateur L'utilisateur non admin
     * @return la liste des résultats
     */
    SortedSet<ResultatTestBio> listResultatsTestBioForUser(Utilisateur utilisateur);

    /**
     * Liste les produits témoins existants pour les résultats de tests biologiques
     * 
     * @return Les organismes
     */
    @Transactional(readOnly = true)
    List<String> listProduitsTemoins();

    /**
     * Vérifie si le test biologique de référence donnée est unique dans la base
     * 
     * @param testBio Le test biologique
     * @return TRUE si le tet biologique est unique
     */
    @Transactional(readOnly = true)
    boolean isTestBioUnique(TestBio testBio);

    /**
     * Charge une manipulation de test biologique
     * 
     * @param idTestBio ID de la manipulation
     * @return La manipulation
     * @throws DataNotFoundException Si non trouvée
     */
    TestBio loadTestBio(Integer idTestBio) throws DataNotFoundException;

    /**
     * Charge une manipulation de test biologique
     * 
     * @param ref Référence de la manipulation
     * @return La manipulation correspondante
     * @throws DataNotFoundException Si non trouvée
     */
    TestBio loadTestBio(@Normalize(UniqueFieldNormalizer.class) String ref) throws DataNotFoundException;

    /**
     * Met à jour une manipulation de test biologique
     * 
     * @param testBio La manipulation
     * @throws DataConstraintException En cas de doublons (champs uniques)
     */
    void updateTestBio(@Normalize(TestBioNormalizer.class) TestBio testBio) throws DataConstraintException;

    /**
     * Rafraichit une testBio (pour éviter des LazyLoadingException)
     * 
     * @param testBio TestBio
     */
    void refreshTestBio(TestBio testBio);

    /**
     * Détermine si un utilisateur peut modifier ou supprimer un test biologique
     * 
     * @param testBio La manipulation
     * @param utilisateur L'utilisateur
     * @return TRUE s'il a le droit
     */
    boolean updateOrdeleteTestBioEnabled(TestBio testBio, Utilisateur utilisateur);

    /**
     * Détermine si un utilisateur peut accéder à un résultat de test biologique
     * 
     * @param resultatTestBio Le résultat
     * @param utilisateur L'utilisateur
     * @return TRUE s'il a le droit
     */
    @Transactional(readOnly = true)
    boolean isResultatTestBioAccessibleByUser(ResultatTestBio resultatTestBio, Utilisateur utilisateur);

    /**
     * Détermine si un résultat est unique par rapport à une une liste (si blanc : unicité sur repère, si témoin :
     * unicité sur repère + produit_témoin, si produit : unicité sur repère + produit)
     * 
     * @param resultatTestBio Le résultat de test bio
     * @param liste La liste des résultats
     * @return TRUE
     */
    @Transactional(readOnly = true)
    boolean isResultatTestBioUniqueInList(final ResultatTestBio resultatTestBio, final List<ResultatTestBio> liste);

    /**
     * Créée une méthode pour un test biologique
     * 
     * @param methode La méthode
     * @throws DataConstraintException Si la méthode (nom) existe déjà
     */
    void createMethodeTestBio(@Normalize(MethodeTestBioNormalizer.class) MethodeTestBio methode)
            throws DataConstraintException;

    /**
     * Supprime une méthode pour un test biologique
     * 
     * @param methode La méthode
     * @throws DataConstraintException En cas de données liées
     */
    void deleteMethodeTestBio(MethodeTestBio methode) throws DataConstraintException;

    /**
     * Liste les méthodes existantes pour un test biologique (triés par nom)
     * 
     * @return la liste des méthodes
     */
    @Transactional(readOnly = true)
    List<MethodeTestBio> listMethodesTestBio();

    /**
     * Liste les domaines existants pour les méthodes de test
     * 
     * @return Les cibles
     */
    @Transactional(readOnly = true)
    List<String> listDomainesMethodes();

    /**
     * Liste les unités de résultat existants pour les méthodes de test
     * 
     * @return Les unités
     */
    @Transactional(readOnly = true)
    List<String> listUnitesResultatMethodes();

    /**
     * Charge une méthode pour un test biologique
     * 
     * @param idMethode ID de la méthode
     * @return La méthode correspondante
     * @throws DataNotFoundException Si non trouvée
     */
    MethodeTestBio loadMethodeTest(Integer idMethode) throws DataNotFoundException;

    /**
     * Charge une méthode pour un test biologique
     * 
     * @param nom Nom de la méthode
     * @return La méthode correspondante
     * @throws DataNotFoundException Si non trouvée
     */
    MethodeTestBio loadMethodeTest(@Normalize(UniqueFieldNormalizer.class) String nom) throws DataNotFoundException;

    /**
     * Met à jour une méthode pour un test biologique
     * 
     * @param methode La méthode
     * @throws DataConstraintException En cas de doublons (champs uniques)
     */
    void updateMethodeTest(@Normalize(MethodeTestBioNormalizer.class) MethodeTestBio methode)
            throws DataConstraintException;

    /**
     * Rafraichit une méthode de testBio (pour éviter des LazyLoadingException)
     * 
     * @param methode La méthode
     */
    void refreshMethodeTestBio(MethodeTestBio methode);

    /**
     * Créée une erreur pour un test biologique
     * 
     * @param erreurTest L'erreur
     * @throws DataConstraintException Si l'erreur (nom) existe déjà
     */
    void createErreurTest(@Normalize(ErreurTestNormalizer.class) ErreurTestBio erreurTest)
            throws DataConstraintException;

    /**
     * Supprime une erreur pour un test biologique
     * 
     * @param erreurTest L'erreur
     * @throws DataConstraintException En cas de données liées
     */
    void deleteErreurTest(ErreurTestBio erreurTest) throws DataConstraintException;

    /**
     * Liste les erreurs existantes pour un test biologique
     * 
     * @return la liste des erreurs
     */
    List<ErreurTestBio> listErreursTestBio();

    /**
     * Charge une erreur pour un test biologique
     * 
     * @param idErreurTest ID de l'erreur
     * @return L'erreur
     * @throws DataNotFoundException Si non trouvée
     */
    ErreurTestBio loadErreurTestBio(Integer idErreurTest) throws DataNotFoundException;

    /**
     * Charge une erreur pour un test biologique
     * 
     * @param nom Nom de l'erreur
     * @return L'erreur
     * @throws DataNotFoundException Si non trouvée
     */
    ErreurTestBio loadErreurTestBio(@Normalize(UniqueFieldNormalizer.class) String nom) throws DataNotFoundException;

    /**
     * Met à jour une erreur pour un test biologique
     * 
     * @param erreurTest L'erreur
     * @throws DataConstraintException En cas de doublons (champs uniques)
     */
    void updateErreurTestBio(@Normalize(ErreurTestNormalizer.class) ErreurTestBio erreurTest)
            throws DataConstraintException;

}
