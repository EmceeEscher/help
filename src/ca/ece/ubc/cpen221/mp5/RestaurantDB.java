package ca.ece.ubc.cpen221.mp5;

import java.util.Set;
import java.util.*;
import java.io.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

// TODO: This class represents the Restaurant Database.
// Define the internal representation and 
// state the rep invariant and the abstraction function.

public class RestaurantDB {
    private Map<String, Restaurant> restaurantMap;
    private Map<String, User> userMap;
    private Map<String, Review> reviewMap;
    
	/**
	 * Create a database from the Yelp dataset given the names of three files:
	 * <ul>
	 * <li>One that contains data about the restaurants;</li>
	 * <li>One that contains reviews of the restaurants;</li>
	 * <li>One that contains information about the users that submitted reviews.
	 * </li>
	 * </ul>
	 * The files contain data in JSON format.
	 * 
	 * @param restaurantJSONfilename
	 *            the filename for the restaurant data
	 * @param reviewsJSONfilename
	 *            the filename for the reviews
	 * @param usersJSONfilename
	 *            the filename for the users
	 */
	public RestaurantDB(String restaurantJSONfilename, String reviewsJSONfilename, 
	        String usersJSONfilename) {
		JSONParser parser = new JSONParser();
		restaurantMap = new HashMap<String, Restaurant>();
		userMap = new HashMap<String, User>();
		reviewMap = new HashMap<String, Review>();
		try{
		    BufferedReader restaurantReader = 
		            new BufferedReader(new FileReader(restaurantJSONfilename));
		    Object nextLine = restaurantReader.readLine();
		    while(nextLine != null){
		        nextLine = parser.parse((String)nextLine);
		        JSONObject currData = (JSONObject) nextLine;
		        Restaurant currRestaurant = new Restaurant(currData);
		        restaurantMap.put(currRestaurant.getBusinessID(), currRestaurant);
		        nextLine = restaurantReader.readLine();
		    }
		    restaurantReader.close();
		    BufferedReader userReader =
		            new BufferedReader(new FileReader(usersJSONfilename));
		    nextLine = userReader.readLine();
		    
		    while(nextLine != null){
		        nextLine = parser.parse((String)nextLine);
		        JSONObject currData = (JSONObject) nextLine;
		        User currUser = new User(currData);
		        userMap.put(currUser.getUserID(), currUser);
		        nextLine = userReader.readLine();
		    }
		    userReader.close();
		    BufferedReader reviewReader =
		            new BufferedReader(new FileReader(reviewsJSONfilename));
		    nextLine = reviewReader.readLine();
		    
		    while(nextLine != null){
		        nextLine = parser.parse((String)nextLine);
		        JSONObject currData = (JSONObject) nextLine;
		        Review currReview = new Review(currData);
		        currReview.setBusinessName(
		                restaurantMap.get(currReview.getBusinessID()).getName());
		        reviewMap.put(currReview.getReviewID(), currReview);
		        nextLine = reviewReader.readLine();
		    }
		    reviewReader.close();
		
		}catch(Exception e){
		    e.printStackTrace();
		}
	}
	/*
	public void testPrint(){
	    for(String key : restaurantMap.keySet()){
	        System.out.println("key: " + key + " Name: " + restaurantMap.get(key).getName());
	    }for(String key : reviewMap.keySet()){
	        System.out.println("key: " + key + " Name: " + reviewMap.get(key).getReviewText());
	    }for(String key : userMap.keySet()){
	        System.out.println("key: " + key + " Name: " + userMap.get(key).getName());
	    }
	}*/
	
	public String processQuery(String query){
	    return "Query processing hasn't been implemented yet.";
	}

	public Set<Restaurant> query(String queryString) {
		// TODO: Implement this method
		// Write specs, etc.
		return null;
	}
	
	/**
	 * Returns a random review for the given restaurant or an error message if
	 * there are no reviews for that restaurant in the database
	 * @param restaurantName: the name of the restaurant to find a review for
	 * @return: a random review for the given restaurant in JSON format, or an
	 * error message in JSON format if there are no reviews for the given
	 * restaurant in the database
	 */
	private JSONObject randomReview(String restaurantName){
	    List<Review> restaurantReviews = new ArrayList<Review>();
	    for(String key : reviewMap.keySet()){
	        if(reviewMap.get(key).getBusinessName().equals(restaurantName))
	            restaurantReviews.add(reviewMap.get(key));
	    }
	    if(restaurantReviews.size() > 0){
	        int randIndex = (int)(Math.random()*restaurantReviews.size());
	        return restaurantReviews.get(randIndex).getJSONInfo();
	    }
	    JSONObject failMessage = new JSONObject();
	    failMessage.put("Message", "No reviews for given restaurant in database");
	    return failMessage;
	}
	
	/**
	 * Returns the information for the restaurant corresponding to the given 
	 * business ID or an error message if there is no restaurant with that
	 * business ID in the database
	 * @param businessID: the alphanumeric business ID of the restaurant to be 
	 * searched for
	 * @return the JSON information of the restaurant corresponding to the given
	 * business ID, or a JSON-formatted error message if there is no restaurant
	 * in the database with the given business ID
	 */
	private JSONObject getRestaurant(String businessID){
	    if(restaurantMap.containsKey(businessID))
	        return restaurantMap.get(businessID).getJSONInfo();
	    JSONObject failMessage = new JSONObject();
	    failMessage.put("Message", "There is no restaurant in the database with"
	            + " that business ID");
	    return failMessage;
	}
	
	/**
	 * Adds a restaurant to the database with the given data or does nothing
	 * if that restaurant is already in the database
	 * @param restaurantInfo is the JSON-formatted data of the restaurant to be
	 * added to the database
	 * @return a JSON-formatted message saying whether or not the restaurant
	 * was successfully added to the database
	 */
	private JSONObject addRestaurant(JSONObject restaurantInfo){
	    Restaurant newRestaurant = new Restaurant(restaurantInfo);
	    for(String key : restaurantMap.keySet()){
	        Restaurant curr = restaurantMap.get(key);
	        if(curr.getAddress().equals(newRestaurant.getAddress()) &&
	                curr.getName().equals(newRestaurant.getName())){
	            JSONObject failMessage = new JSONObject();
	            failMessage.put("Message", "There is already a restaurant with"
	                    + " that name and address in the database.");
	            return failMessage;
	        }
	    }
	    restaurantMap.put(newRestaurant.getBusinessID(), newRestaurant);
	    JSONObject successMessage = new JSONObject();
	    successMessage.put("Message", "Restaurant added to database.");
	    return successMessage;        
	}
	
	/**
     * Adds a user with the given data to the database or does nothing
     * if that user is already in the database
     * @param userInfo is the JSON-formatted data of the user to be
     * added to the database
     * @return a JSON-formatted message saying whether or not the user
     * was successfully added to the database
     */
	private JSONObject addUser(JSONObject userInfo){
	    User newUser = new User(userInfo);
	    for(String key : userMap.keySet()){
	        User curr = userMap.get(key);
	        if(curr.getUserID().equals(newUser.getUserID())){
	            JSONObject failMessage = new JSONObject();
	            failMessage.put("Message", "That user is already in the database.");
	            return failMessage;
	        }
	    }
	    userMap.put(newUser.getUserID(), newUser);
	    JSONObject successMessage = new JSONObject();
	    successMessage.put("Message", "User added to database.");
	    return successMessage;
	}
	
	/**
     * Adds a review with the given data to the database or does nothing
     * if that review is already in the database
     * @param reviewInfo is the JSON-formatted data of the review to be
     * added to the database
     * @return a JSON-formatted message saying whether or not the review
     * was successfully added to the database
     */
	private JSONObject addReview(JSONObject reviewInfo){
	    Review newReview = new Review(reviewInfo);
	    for(String key : reviewMap.keySet()){
	        Review curr = reviewMap.get(key);
	        if(curr.getReviewID().equals(newReview.getReviewID())){
	            JSONObject failMessage = new JSONObject();
	            failMessage.put("Message", "That review is already in the database.");
	            return failMessage;
	        }
	    }
	    reviewMap.put(newReview.getReviewID(), newReview);
	    JSONObject successMessage = new JSONObject();
	    successMessage.put("Message", "Review added to database.");
	    return successMessage;
	}
}


