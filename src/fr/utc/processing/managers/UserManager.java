package fr.utc.processing.managers;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import fr.utc.dataStructure.*;
import fr.utc.exceptions.NetworkSocketException;
import fr.utc.exceptions.NullUserNetworkException;
import sun.util.logging.resources.logging;

public class UserManager {
	/**
	 * users is the list of the whole users currently logged in
	 */
	private ArrayList<UserNetwork> users;
	private Logger logger;

	/**
	 * currentUser is the current user logged in
	 */
	private UserLocal currentUser;

	private ProcessingManager processingManager;

	private final String FILEUSERDIRECTORY = System.getProperty("user.home")+File.separator+"sudoku_AI12";

	public UserManager(){
		this.users = new ArrayList<>();
		logger = Logger.getLogger(getClass().getName());
	}

	public void setProcessingManager(ProcessingManager processingManager)
	{
		this.processingManager = processingManager;
	}

	public ProcessingManager getProcessingManager()
	{
		return processingManager;
	}

	/**
	 * Method which returns all the users logged in
	 * @return
	 */
	public List<UserNetwork> getAllUsers(){
		return this.users;
	}

	/**
	 * Method which returns the current local user logged in
	 * @return
	 */
	public UserLocal getCurrentUser(){
		return currentUser;
	}

	/**
	 * Method which create a user local and add it to the list of users
	 * @param uuid
	 * @param login
	 * @param password
	 * @param firstname
	 * @param lastname
	 * @param birthday
	 * @param avatar
	 * @param uri
	 */
	public void createUser(String login, String password, String firstname, String lastname, Date birthday){
		UserLocal userLocalTest;
		userLocalTest = readUserCSV(login,password);
		if(userLocalTest == null){
			UserLocal userLocal = new UserLocal(login,birthday,lastname,firstname,password);
			writeUserCSV(userLocal);
		}
	}

	public void writeUserCSV(UserLocal currentUser){
		String fileDirectory = FILEUSERDIRECTORY;
		File file = new File(fileDirectory);
		if(!file.exists() || !file.isDirectory())
		{
			file.mkdir();
		}

		String fileName = FILEUSERDIRECTORY+File.separator+"UserSudoku.csv";
		File usersFile = new File(fileName);

		if(!usersFile.exists())
		{
			FileOutputStream  fos = null;
			try {
				fos = new FileOutputStream(usersFile);
			} catch (FileNotFoundException e) {
				logger.log(Level.SEVERE, e.toString(), e);
			}
			finally {
				try {
					if (fos != null ) 
						fos.close();
				} catch (IOException e) {
					logger.log(Level.SEVERE, e.toString(), e);
				}
			}
		}

		try (FileWriter fileWriter = new FileWriter(fileName,true)) {
			fileWriter.append(currentUser.toStringCSV());
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
		} 
	}

	public void updateUserCsv(UserLocal currentUser){
		String fileName = FILEUSERDIRECTORY+File.separator+"UserSudoku.csv";
		File usersFile = new File(fileName);

		if(usersFile.exists() && !usersFile.isDirectory()){	
			try {
				List<String> lines = Files.readAllLines(Paths.get(fileName),Charset.forName("UTF-8"));
				Files.delete(Paths.get(fileName));
				UserLocal tmpUser;

				for(String line: lines){
					String[] tokens = line.split(";");
					if (tokens.length > 0) {
						DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
						Date bday = null;
						if(!"".equals(tokens[4]))
						{
							bday = df.parse(tokens[4]);
						}
						tmpUser = new UserLocal(tokens[1],bday,tokens[2],tokens[3],null,tokens[6]);
						tmpUser.setUuid(UUID.fromString(tokens[0]));

						if(tmpUser.getUuid().toString().equals(currentUser.getUuid().toString()))
							tmpUser = currentUser;

						writeUserCSV(tmpUser);
					}
				}
			} 
			catch (IOException | ParseException e) {
				logger.log(Level.SEVERE, e.toString(), e);
			}
		}
	}

	public UserLocal readUserCSV(String login,String password){
		String fileDirectory = FILEUSERDIRECTORY;
		File file = new File(fileDirectory);
		if(!file.exists() || !file.isDirectory())
		{
			file.mkdir();
		}

		String fileName = FILEUSERDIRECTORY+File.separator+"UserSudoku.csv";
		File usersFile = new File(fileName);

		if(usersFile.exists() && !usersFile.isDirectory())
		{
			UserLocal userTofind = null;
			try (BufferedReader fileReader = new BufferedReader(new FileReader(fileName))){
				String line = "";
				//Read the file line by line
				while ((line = fileReader.readLine()) != null) {
					//Get all tokens available in line
					String[] tokens = line.split(";");
					if (tokens.length > 0) {
						//test login password
						if(login.equals(tokens[1]) && password.equals(tokens[6])){
							fileReader.close();
							//init user
							DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
							Date bday = null;
							if(!"".equals(tokens[4]))
							{
								bday = df.parse(tokens[4]);
							}
							userTofind = new UserLocal(tokens[1],bday,tokens[2],tokens[3],null,tokens[6]);
							userTofind.setUuid(UUID.fromString(tokens[0]));
							return userTofind;
						}

					}
				}
			}
			catch(Exception e){
				logger.log(Level.SEVERE, e.toString(), e);
			}
		}
		return null;
	}
	public UserLocal createTestUser(String login, String password, String firstname, String lastname, Date birthday, URI uri){
		return new UserLocal(login, birthday, lastname, firstname, uri, password);
	}

	/**
	 * Method which add a remote user to the list of current users logged in
	 * @param userNetwork
	 * @throws NullUserNetworkException
	 */
	public void addUserToList(UserNetwork userNetwork) throws NullUserNetworkException{
		if(userNetwork != null){
			this.users.add(userNetwork);
		} else {
			throw new NullUserNetworkException("The UserNetwork you are trying to add is null");
		}
	}


	/**
	 * Method which modifies information about the current local user
	 * @param login
	 * @param password
	 * @param firstname
	 * @param lastname
	 * @param birthday
	 * @param avatar
	 */
	public void modifyLocalUser(String login, String password, String firstname, String lastname, Date birthday, BufferedImage avatar){
		if(birthday != null)
			this.currentUser.setBirthday(birthday);
		if(lastname != null && !lastname.isEmpty())
			this.currentUser.setLastName(lastname);
		if(firstname != null && !firstname.isEmpty())
			this.currentUser.setFirstName(firstname);
		if(password != null && !password.isEmpty())
			this.currentUser.setPassword(password);
		if(login != null && !login.isEmpty())
			this.currentUser.setLogin(login);
	}

	/**
	 * Method used to log out the current user local.
	 * It's needed to delete all the data which will be overwritten by the next authentication
	 */
	public void clear(){
		this.users.clear();
		this.currentUser = null;
	}

	/**
	 * 
	 * @param user
	 */
	public void setUserLocal(UserLocal user)
	{
		currentUser = user;
	}

	/**
	 * Get the user from UUID
	 * @param Uuid
	 * @return
	 */
	public UserNetwork getUserNetworkFromUuid(String Uuid){
		for(UserNetwork user : getAllUsers())
			if(user.getUuid().toString().equals(Uuid))
				return user;
		return null;
	}


	public UserNetwork getUserByUuid(String uuid) throws NetworkSocketException {
		if (uuid != null) {
			List<UserNetwork> allUsers = this.getAllUsers();
			return allUsers.stream().filter(user -> user.getUuid().toString().equals(uuid)).findFirst().orElse(null);
		} else {
			return null;
		}
	}

	public void saveUris() {
		String fileDirectory = System.getProperty("user.home")+"/sudoku_AI12/";
		File file = new File(fileDirectory);
		if(!file.exists() || !file.isDirectory())
		{
			file.mkdir();
		}
		
		try {
			UserLocal userLocal = processingManager.getUserManager().getCurrentUser();
			String fileName = fileDirectory+userLocal.getUuid()+".uris";
			File urisFile = new File(fileName);

			Set<URI> uris = getCurrentUser().getUserNodes();
			FileOutputStream fos = new FileOutputStream(urisFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(uris);
			oos.close();
			fos.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}

	public void loadUris()
	{
		String fileDirectory = System.getProperty("user.home")+"/sudoku_AI12/";
		File file = new File(fileDirectory);
		if(!file.exists() || !file.isDirectory())
		{
			file.mkdir();
		}

		try {
			UserLocal userLocal = processingManager.getUserManager().getCurrentUser();
			String fileName = fileDirectory+userLocal.getUuid()+".uris";
			File urisFile = new File(fileName);
			if(urisFile.exists() && !urisFile.isDirectory())
			{
				FileInputStream fis = new FileInputStream(urisFile);
				ObjectInputStream ois = new ObjectInputStream(fis);

				@SuppressWarnings("unchecked")
				Set<URI> allUris = (Set<URI>)ois.readObject();
				ois.close();
				fis.close();

				Iterator<URI> it = allUris.iterator();

				while (it.hasNext()) {
					URI uri = it.next();

					this.getCurrentUser().addUserNode(uri);;
				}
			}
		} catch (ClassNotFoundException | IOException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}
}
