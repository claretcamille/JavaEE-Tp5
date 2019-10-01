package simplejdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

public class DAO {

	private final DataSource myDataSource;

	/**
	 *
	 * @param dataSource la source de données à utiliser
	 */
	public DAO(DataSource dataSource) {
		this.myDataSource = dataSource;
	}

	/**
	 *
	 * @return le nombre d'enregistrements dans la table CUSTOMER
	 * @throws DAOException
	 */
	public int numberOfCustomers() throws DAOException {
		int result = 0;

		String sql = "SELECT COUNT(*) AS NUMBER FROM CUSTOMER";
		// Syntaxe "try with resources" 
		// cf. https://stackoverflow.com/questions/22671697/try-try-with-resources-and-connection-statement-and-resultset-closing
		try (Connection connection = myDataSource.getConnection(); // Ouvrir une connexion
			Statement stmt = connection.createStatement(); // On crée un statement pour exécuter une requête
			ResultSet rs = stmt.executeQuery(sql) // Un ResultSet pour parcourir les enregistrements du résultat
		) {
                                                                         
			rs.next(); // Pas la peine de faire while, il y a 1 seul enregistrement
                                                                        // On récupère le champ NUMBER de l'enregistrement courant
                                                                        result = rs.getInt("NUMBER");
                                                                        

		} catch (SQLException ex) {
			Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
			throw new DAOException(ex.getMessage());
		}

		return result;
	}

	/**
	 * Detruire un enregistrement dans la table CUSTOMER
	 *
	 * @param customerId la clé du client à détruire
	 * @return le nombre d'enregistrements détruits (1 ou 0 si pas trouvé)
	 * @throws DAOException
	 */
	public int deleteCustomer(int customerId) throws DAOException {

		// Une requête SQL paramétrée
		String sql = "DELETE FROM CUSTOMER WHERE CUSTOMER_ID = ?";
		try (Connection connection = myDataSource.getConnection();
			PreparedStatement stmt = connection.prepareStatement(sql)) {
			// Définir la valeur du paramètre
			stmt.setInt(1, customerId);

			return stmt.executeUpdate();

		} catch (SQLException ex) {
			Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
			throw new DAOException(ex.getMessage());
		}
	}

	/**
	 *
	 * @param customerId la clé du client à recherche
	 * @return le nombre de bons de commande pour ce client (table PURCHASE_ORDER)
	 * @throws DAOException
	 */
	public int numberOfOrdersForCustomer(int customerId) throws DAOException {
		int result = 0;
                                                String sql = "SELECT COUNT(*) AS NUMBER FROM PURCHASE_ORDER WHERE CUSTOMER_ID = ?";
                                                try(                
                                                                        Connection c = this.myDataSource.getConnection(); // Connexion a la bd 
			PreparedStatement stmt = c.prepareStatement(sql); //creation et preparation du statement selon la commande sql
                                                 ) {
                                                                        // Définir la valeur du paramètre
			stmt.setInt(1, customerId);
			ResultSet rs = stmt.executeQuery() ;// Parcourt
                                                                        
                                                                        if(rs.next()){ // Pas la peine de faire while, il y a 1 seul enregistrement
                                                                                                                        // On récupère le champ NUMBER de l'enregistrement courant
                                                                                                                         result = rs.getInt("NUMBER");
                                                                                                

                                                                       }

		} catch (SQLException ex) {
			Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
			throw new DAOException(ex.getMessage());
		}

                                                return result;
	}

	/**
	 * Trouver un Customer à partir de sa clé
	 *
	 * @param customerID la clé du CUSTOMER à rechercher
	 * @return l'enregistrement correspondant dans la table CUSTOMER, ou null si pas trouvé
	 * @throws DAOException
	 */
	CustomerEntity findCustomer(int customerID) throws DAOException {
		CustomerEntity result = null; // Initialisation a null dans le cas ou on ne trouve pas comme ça on a la bonne valeur
                                                String sql =  "SELECT * FROM CUSTOMER WHERE CUSTOMER_ID = ?";
                                                
                                                try(
                                                            Connection c  = this.myDataSource.getConnection();
                                                            PreparedStatement stmt = c.prepareStatement(sql);
                                                 ){
                                                                        stmt.setInt(1, customerID);
                                                                        ResultSet rs = stmt.executeQuery();
			if (rs.next()) { // Alors l'id existe
				String name = rs.getString("NAME");
				String address = rs.getString("ADDRESSLINE1");
				// On implémente le résultat qui est différent de null
                                                                                                result = new CustomerEntity(customerID, name, address);
				
                                                                         }
                                                    
                                                } catch (SQLException ex) {
			Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
			throw new DAOException(ex.getMessage());
		}
                                                return result;
	}

	/**
	 * Liste des clients localisés dans un état des USA
	 *
	 * @param state l'état à rechercher (2 caractères)
	 * @return la liste des clients habitant dans cet état
	 * @throws DAOException
	 */
	List<CustomerEntity> customersInState(String state) throws DAOException {
		List<CustomerEntity> result = new ArrayList<>();
                                                String sql = "SELECT * FROM CUSTOMER WHERE STATE = ?";
                                                try(
                                                            Connection c = this.myDataSource.getConnection();
                                                            PreparedStatement stmt = c.prepareStatement(sql);
                                                ){
                                                                        stmt.setString(1, state);
                                                                        ResultSet rs = stmt.executeQuery();
                                                                       while (rs.next()) { // Alors l'id existe
                                                                                                int customerID = rs.getInt("CUSTOMER_ID");
				String name = rs.getString("NAME");
				String address = rs.getString("ADDRESSLINE1");
				// On implémente le résultat qui est différent de null
                                                                                                result.add(new CustomerEntity(customerID, name, address));
				
                                                                         } 
                                                                        
                                                    
                                                }catch (SQLException ex) {
			Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
			throw new DAOException(ex.getMessage());
		}
                                                return result;
	}

}
