package edu.ucalgary.ensf409;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class CreateOrder {
    private Order originalRequest;
    private PrintWriter outStream;
    private String[] itemsOrdered;
    private int totalPrice;
    Database db;
    private ArrayList<ArrayList<Integer>> combinations = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> prices = new ArrayList<Integer>();

    /**
     * 3 argument constructor for the Create order class
     * creates a file for outputting order results, using a
     * custom file number ID to ensure the file is never over-written
     * even upon new runs of the program. The connection to the database is also
     * made here so that item IDs and Manufacturer IDs can be accessed
     *
     * @param request the order generated by the switch in main should be passed in here
     * @param ldt pass in a LocalDateTime object to generate a custom order ID
     * @param db the database object passed in by the Database class
     */
    CreateOrder(Order request, LocalDateTime ldt, Database db)
    {
        //Set request
        this.originalRequest = request;
        //generate unique file ID
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH~mm~ss");
        String formatDateTime = ldt.format(format);
        //Create output Filepath
        try
        {
            this.outStream = new PrintWriter(new File(request.getFurnitureType()+"_"+request.getFurnitureCategory()+"_Order_["+ formatDateTime + "].txt"));
        }
        catch (IOException e)
        {
            System.out.println("Error opening output file");
            System.exit(1);
        }
        this.db = db;
    }

    /**
     * Setter method for the private Order Object
     * @param request Order object to set
     */
    public void setOriginalRequest(Order request){
        this.originalRequest = request;
    }

    /**
     * returns the Order object stored privately in the class
     * @return Order object
     */
    public Order getOriginalRequest() {
        return this.originalRequest;
    }

    /**
     * Setter method for items ordered array
     * @param toOrder
     */
    public void setItemsOrdered(String[] toOrder) {
        if(toOrder != null) {
            itemsOrdered = new String[toOrder.length];
            System.arraycopy(toOrder, 0, itemsOrdered, 0, toOrder.length);
        }
    }

    /**
     * returns the stored String array data field containing
     * the Item IDs of the items ordered
     * @return A string array
     */
    public String[] getItemsOrdered() {
        return this.itemsOrdered;
    }

    /**
     * setter method for the total price of the order
     * @param price integer representing price of the order
     */
    public void setTotalPrice(int price){
        this.totalPrice = price;
    }

    /**
     * getter method for the total order price
     * @return an Integer
     */
    public int getTotalPrice() {
        return this.totalPrice;
    }

    /**
     * Creates an outputfile with all the relevant data
     * including price, and items ordered referenced by
     * Item ID passed in via private data methods
     */
    public void generateOrder() {
        //Order form header
        outStream.println("Furniture Order Form");
        outStream.println();
        outStream.println("Faculty Name:");
        outStream.println("Contact:");
        outStream.println("Date:");
        outStream.println();
        //State client's order
        outStream.println("Original Request: " + originalRequest.getFurnitureType() +" "+ originalRequest.getFurnitureCategory()+ ", " + originalRequest.getNumberItems());
        outStream.println();
        //State order solution
        outStream.println("Items Ordered");
        for (String s : this.itemsOrdered) {
            outStream.println("ID: " + s);
        }
        outStream.println();
        // generate receipt
        outStream.println("Total Price: $" + this.totalPrice+".00");
        //close file
        outStream.close();
    }
    /**
     * Called from main in the case that an order cannot be created using the current inventory
     * uses privately held data members to use the Method: suppliersOf to find which manufacuturers can produce
     * the specified order, this data is then used to generate an alternative output file.
     */
    public void generateRecommendation() {
        //Sate failure to oblige order
        outStream.println("Original request cannot be completed due to current inventory");
        outStream.println();
        //State clients order
        outStream.println("Original Request: " + originalRequest.getFurnitureType() +" "+ originalRequest.getFurnitureCategory()+ "," + originalRequest.getNumberItems());
        outStream.println();
        //Recommend alternative solution to complete order
        outStream.println("To complete your order please contact the following UofC approved suppliers: ");
        //Pass to find suppliers of requested order
        outStream.println(suppliersOf(this.originalRequest.getFurnitureCategory(), this.originalRequest.getFurnitureType()));
        outStream.close();
    }

    /**
     * Called from the generateRecommendation method to fill in the data of which manufacturers can
     * produce the requested order.
     * @param category A String representing the category of furniture to search for e.g. mesh for type chair (see type)
     * @param type A String representing the type of furniture to search for e.g. chair
     * @return A Sting containing a list of all suppliers of the requested item with each supplier on their own line
     */
    public String suppliersOf(String category, String type) {
        //Create new data structure for temporary storage
        ArrayList<String> suppliers = new ArrayList<String>();
        //Create return String
        String toRet = "";
        //outer check furniture category
        if(category.equals("chair")) {
            //check all chairs in database
            for (int i = 0; i < this.db.getChairs().length; i++) {
                //check for matching type
                if (this.db.getChairs()[i].getType().equals(type)) {
                    //check all manufacturers
                    for(int j = 0; j < this.db.getManufacturers().length; j++) {
                        //find corresponding manufacturer base on ID
                        if(this.db.getChairs()[i].getManuId().equals(this.db.getManufacturers()[j].getManuId())) {
                            //Assert that the matching manufacturer is not already in the the list
                            if(!suppliers.contains(this.db.getManufacturers()[j].getName())) {
                                //add to list
                                suppliers.add(this.db.getManufacturers()[j].getName());
                            }
                        }
                    }
                }
            }
        }else if (category.equals("desk")) { //check furniture category
            for (int i = 0; i < this.db.getDesk().length; i++) { //check for matching type
                if (this.db.getDesk()[i].getType().equals(type)) { //proceed if match found
                    for (int j = 0; j < this.db.getManufacturers().length; j++) { //check for manufacturer associated with matching item
                        if (this.db.getDesk()[i].getManuId().equals(this.db.getManufacturers()[j].getManuId())) { //proceed if match
                            if (!suppliers.contains(this.db.getManufacturers()[j].getName())) { //proceed if not already in list
                                suppliers.add(this.db.getManufacturers()[j].getName()); //add to list
                            }
                        }
                    }
                }
            }
        } else if(category.equals("filing")){ //check furniture category
            for (int i = 0; i < this.db.getFilings().length; i++) { //check for matching type
                if (this.db.getFilings()[i].getType().equals(type)) { //proceed if match found
                    for(int j = 0; j < this.db.getManufacturers().length; j++) { //check for manufacturer associated with matching item
                        if(this.db.getFilings()[i].getManuId().equals(this.db.getManufacturers()[j].getManuId())) { //proceed if match
                            if(!suppliers.contains(this.db.getManufacturers()[j].getName())) { //proceed if not already in list
                                suppliers.add(this.db.getManufacturers()[j].getName()); //add to list
                            }
                        }
                    }
                }
            }
        } else if (category.equals("lamp")) { //check furniture category
            for (int i = 0; i < this.db.getLamps().length; i++) { //check for matching type
                if (this.db.getLamps()[i].getType().equals(type)) { //proceed if match found
                    for(int j = 0; j < this.db.getManufacturers().length; j++) { //check for manufacturer associated with matching item
                        if(this.db.getLamps()[i].getManuId().equals(this.db.getManufacturers()[j].getManuId())) { //proceed if match
                            if(!suppliers.contains(this.db.getManufacturers()[j].getName())) { //proceed if not already in list
                                suppliers.add(this.db.getManufacturers()[j].getName()); //add to list
                            }
                        }
                    }
                }
            }
        }
        //fill return string with supplier names from the array list concatenated together
        for(int i = 0; i < suppliers.size(); i++) {
            toRet = toRet.concat(suppliers.get(i) + "\n");
        }
        //return
        return toRet;
    }

    /**
     * This method searches through the prices ArrayList for the lowest value.
     * @return lowest price in the ArrayList prices.
     */
    public int getLowestPrice(){
        int lowest = 0;
        if(prices.size() >= 1){
            lowest = prices.get(0);
        }
        for (Integer price : prices) {
            if (price <= lowest) {
                lowest = price;
            }
        }
        return lowest;
    }

    /**
     * This method returns the combination inside the ArrayList combinations the corresponds with the lowest price.
     * @return ArrayList of the the indexes in the database furniture table that make up the combination.
     */
    public ArrayList<Integer> getLowestCombination(){
        return combinations.get(prices.indexOf(this.totalPrice));
    }

    /**
     * this method clears the ArrayLists prices and combinations
     */
    public void clearLists(){
        prices.clear();
        combinations.clear();
    }

    /**
     * This method takes in a ArrayList of Integers and an integer called indicator which tells the method what furniture
     * category array to look in. The method returns a String array of the Id's for each furniture item corresponding to the
     * table index numbers in the arraylist.
     * @param itemIndexes Stores the indexes of the items used for the requested order
     * @param indicator Store a number from 0-3 indicating what furniture category to look in.
     * @return String Array of ID numbers corresponding to the Table indexes stored in the ArrayList
     */
    public String[] makeIdArray(ArrayList<Integer> itemIndexes, int indicator){
        String ids[] = new String[itemIndexes.size()];
        switch(indicator){
            case 0:
                for(int i = 0; i < itemIndexes.size(); i++){
                    ids[i] = db.getDesk()[itemIndexes.get(i)].getId();
                }
                break;
            case 1:
                for(int i = 0; i < itemIndexes.size(); i++){
                    ids[i] = db.getChairs()[itemIndexes.get(i)].getId();
                }
                break;
            case 2:
                for(int i = 0; i < itemIndexes.size(); i++){
                    ids[i] = db.getFilings()[itemIndexes.get(i)].getId();
                }
                break;
            default:
                for(int i = 0; i < itemIndexes.size(); i++){
                    ids[i] = db.getLamps()[itemIndexes.get(i)].getId();
                }
                break;
        }
        return ids;
    }

    /**
     * This method checks if the Arraylist arr contains the value i inside it.
     * @param arr ArrayList of Integers
     * @param index value being checked
     * @return true if the value is not in the ArrayList, false otherwise.
     */
    public boolean newEvent(ArrayList<Integer> arr, int index){
        boolean didNotHappen = true;
        for(int j = 0; j < arr.size(); j++){
            if(index == arr.get(j)){
                didNotHappen = false;
            }
        }
        return didNotHappen;
    }


    /**
     * chairPrice recursively searches through the chair array to find the lowest price of all combinations to make the desired order for the
     * user. When a combination is found, it inserts and ArrayList of the combination indexes into a 2D ArrayList called
     * combinations. It also puts the price sum of that found combination into an ArrayList called prices and checks for the lowest
     * price within that list of prices.
     * @param table // array of chair objects, replicating the chair table in the database
     * @param priceTotal // price of combined items
     * @param alreadyHit // stores the indexes of the chairs already checked
     * @param type // furniture type
     * @param number // number of desired furniture items
     * @param legs // counts the number of legs found
     * @param arms // counts the number of arms found
     * @param seats // counts the number of seats found
     * @param cushions // counts the number of cushions found
     */
    public int chairPrice(Chair[] table, int priceTotal, ArrayList<Integer>alreadyHit, String type, int number,
                           int legs, int arms, int seats, int cushions) {
        int lowest = getLowestPrice();
        int totalPrice2 = priceTotal; // saves the price total for each recursion stage
        // search through the chair array to find a chair of the desired type
        for (int i = 0; i < table.length; i++) {
            int lCount = legs; // saves the amount of legs found for this recursion stage
            int aCount = arms; // saves the amount of arms found for this recursion stage
            int sCount = seats; // saves the amount of seats found for this recursion stage
            int cCount = cushions; // saves the amount of cushions found for this recursion stage
            ArrayList<Integer>alreadyHit2 = new ArrayList<Integer>(alreadyHit); // saves the already checked chairs for this recursion stage
            if(table[i].getType().equals(type)){ // if the chair at current index matches the desired chair type
                if(newEvent(alreadyHit, i)){ // if the chair at current index hasn't been checked already
                    alreadyHit2.add(i); // add the current index to the ArrayList of checked array elements

                    // If chair at current index has legs and that max number of legs needed has not been reached
                    if(db.getChairs()[i].getLegs().equals("Y") && legs < number){
                        lCount = legs+1; // add legs to the amount of legs found
                    }
                    // If chair at current index has arms and that max number of arms needed has not been reached
                    if(db.getChairs()[i].getArms().equals("Y") && arms < number){
                        aCount = arms+1; // add arms to amount of arms found
                    }
                    // If chair at current index has a seat and that max number of seats needed has not been reached
                    if(db.getChairs()[i].getSeat().equals("Y") && seats < number){
                        sCount = seats+1; // add seat to the amount of seats found
                    }
                    // If chair at current index has a cushion and that max number of cushions needed has not been reached
                    if(db.getChairs()[i].getCushion().equals("Y") && cushions < number){
                        cCount = cushions+1; // add cushion to the amount of cushions found
                    }
                    // adds the chair price at current index to price sum and is saved for current recursion call
                    totalPrice2 = priceTotal + db.getChairs()[i].getPrice();
                    // if the amount legs + arms + seats + cushions found is equal to max amount pieces needed to make order
                    if(lCount+aCount+sCount+cCount == number*4){
                        prices.add(totalPrice2); // add total price of combination to prices ArrayList
                        combinations.add(alreadyHit2); // add the indexes of the combined chairs to the combinations ArrayList
                        lowest = getLowestPrice(); // Update lowest chair price
                        return lowest;  // return updated lowest price
                    }
                    lowest = chairPrice(table, totalPrice2, alreadyHit2, type, number, lCount, aCount, sCount, cCount); // recursive call
                }
            }
        }
        return lowest; // return updated lowest price
    }

    /**
     * deskPrice recursively searches through the desks array to find the lowest price of all combinations to make the desired order for the
     * user. When a combination is found, it inserts and ArrayList of the combination indexes into a 2D ArrayList called
     * combinations. It also puts the price sum of that found combination into an ArrayList called prices and checks for the lowest
     * price within that list of prices.
     * @param table // array of desk objects, replicating the desk table in the database
     * @param priceTotal // price of combined items
     * @param alreadyHit // stores the indexes of the desks already checked
     * @param type // furniture type
     * @param number // number of desired furniture items
     * @param legs // counts the number of legs found
     * @param tops // counts the number of tops found
     * @param drawers // counts the number of drawers found
     */
    public int deskPrice(Desk[] table, int priceTotal, ArrayList<Integer>alreadyHit, String type, int number,
                          int legs, int tops, int drawers) {
        int lowest = getLowestPrice();
        int totalPrice2 = priceTotal; // saves the price total for each recursion stage
        // search through the desk array to find a desk of the desired type
        for (int i = 0; i < table.length; i++) {
            int lCount = legs; // saves the amount of legs found for this recursion stage
            int tCount = tops; // saves the amount of tops found for this recursion stage
            int dCount = drawers; // saves the amount of drawers found for this recursion stage
            ArrayList<Integer>alreadyHit2 = new ArrayList<Integer>(alreadyHit); // saves the already checked desks for this recursion stage
            if(table[i].getType().equals(type)){ // if the desk at current index matches the desired desk type
                if(newEvent(alreadyHit, i)){ // if the desk at current index hasn't been checked already
                    alreadyHit2.add(i); // add the current index to the ArrayList of checked array elements

                    // If desk at current index has legs and that max number of legs needed has not been reached
                    if(table[i].getLegs().equals("Y") && legs < number){
                        lCount = legs+1; // add legs to the amount of legs found
                    }

                    // If desk at current index has tops and that max number of tops needed has not been reached
                    if(table[i].getTop().equals("Y") && tops < number){
                        tCount = tops+1; // add top to the amount of tops found
                    }

                    // If desk at current index has drawers and that max number of drawers needed has not been reached
                    if(table[i].getDrawer().equals("Y") && drawers < number){
                        dCount = drawers+1; // add drawer to the amount of drawers found
                    }
                    // adds the filing price at current index to price sum and is saved for current recursion call
                    totalPrice2 = priceTotal + table[i].getPrice();
                    // if the amount legs + tops + drawers found is equal to max amount pieces needed to make order
                    if(lCount+tCount+dCount == number*3){
                        prices.add(totalPrice2); // add total price of combination to prices ArrayList
                        combinations.add(alreadyHit2); // add the indexes of the combined desks to the combinations ArrayList
                        lowest = getLowestPrice(); // Update lowest desk price
                        return lowest; // return updated lowest price
                    }
                    lowest = deskPrice(table, totalPrice2, alreadyHit2, type, number, lCount, tCount, dCount); // recursive call
                }
            }
        }
        return lowest; // return updated lowest price
    }

    /**
     * filingPrice recursively searches through the filing array to find the lowest price of all combinations to make the desired order for the
     * user. When a combination is found, it inserts and ArrayList of the combination indexes into a 2D ArrayList called
     * combinations. It also puts the price sum of that found combination into an ArrayList called prices and checks for the lowest
     * price within that list of prices.
     * @param table // array of filing objects, replicating the filing table in the database
     * @param priceTotal // price of combined items
     * @param alreadyHit // stores the indexes of the filings already checked
     * @param type // furniture type
     * @param number // number of desired furniture items
     * @param rails //  counts the number of rails found
     * @param drawers // counts the number of drawers found
     * @param cabinets // counts the number of cabinets found
     */
    public int filingPrice(Filing[] table, int priceTotal, ArrayList<Integer>alreadyHit, String type, int number,
                            int rails, int drawers, int cabinets) {
        int lowest = getLowestPrice();
        int totalPrice2 = priceTotal; // saves the price total for each recursion stage
        for (int i = 0; i < table.length; i++) {
            // search through the filing array to find a filing of the desired type
            int rCount = rails; // saves the amount of rails found for this recursion stage
            int dCount = drawers; // saves the amount of drawers found for this recursion stage
            int cCount = cabinets; // saves the amount of cabinets found for this recursion stage
            ArrayList<Integer>alreadyHit2 = new ArrayList<Integer>(alreadyHit); // saves the already checked filings for this recursion stage
            if(table[i].getType().equals(type)){ // if the filing at current index matches the desired filing type
                if(newEvent(alreadyHit, i)){ // if the filing at current index hasn't been checked already
                    alreadyHit2.add(i); // add the current index to the ArrayList of checked array elements

                    // If filing at current index has rails and that max number of rails needed has not been reached
                    if(table[i].getRails().equals("Y") && rails < number){
                        rCount = rails+1; // add rails to the amount of rails found
                    }

                    // If filing at current index has drawers and that max number of drawers needed has not been reached
                    if(table[i].getDrawers().equals("Y") && drawers < number){
                        dCount = drawers+1; // add drawer to the amount of drawers found
                    }

                    // If filing at current index has cabinets and that max number of cabinets needed has not been reached
                    if(table[i].getCabinet().equals("Y") && cabinets < number){
                        cCount = cabinets+1; // add cabinet to the amount of cabinets found
                    }
                    // adds the filing price at current index to price sum and is saved for current recursion call
                    totalPrice2 = priceTotal + table[i].getPrice();
                    // if the amount rails + drawers + cabinets found is equal to max amount pieces needed to make order
                    if(rCount+dCount+cCount == number*3){
                        prices.add(totalPrice2); // add total price of combination to prices ArrayList
                        combinations.add(alreadyHit2); // add the indexes of the combined filings to the combinations ArrayList
                        lowest = getLowestPrice(); // Update lowest filing price
                        return lowest; // return updated lowest price
                    }
                    lowest = filingPrice(table, totalPrice2, alreadyHit2, type, number, rCount, dCount, cCount); // recursive call
                }
            }
        }
        return lowest; // return updated lowest price
    }

    /**
     * lampPrice recursively searches through the lamps array to find the lowest price of all combinations to make the desired order for the
     * user. When a combination is found, it inserts and ArrayList of the combination indexes into a 2D ArrayList called
     * combinations. It also puts the price sum of that found combination into an ArrayList called prices and checks for the lowest
     * price within that list of prices.
     * @param table // array of lamp objects, replicating the lamp table in the database
     * @param priceTotal // price of combined items
     * @param alreadyHit // stores the indexes of the lamps already checked
     * @param type // furniture type
     * @param number // number of desired furniture items
     * @param bases // counts the number of bases found
     * @param lightBulbs // counts the number of bulbs found
     */
    public int lampPrice(Lamp[] table, int priceTotal, ArrayList<Integer>alreadyHit, String type, int number,
                          int bases, int lightBulbs) {
        int lowest = getLowestPrice();
        int totalPrice2 = priceTotal; // saves the price total for each recursion stage
        // search through the lamp array to find a lamp of the desired type
        for (int i = 0; i < table.length; i++) {
            int bCount = bases; // saves the amount of bases found for this recursion stage
            int lCount = lightBulbs; // saves the amount of bulbs found for this recursion stage
            ArrayList<Integer>alreadyHit2 = new ArrayList<Integer>(alreadyHit); // saves the already checked lamps for this recursion stage
            if(table[i].getType().equals(type)){ // if the lamp at current index matches the desired lamp type
                if(newEvent(alreadyHit, i)){ // if the lamp at current index hasn't been checked already
                    alreadyHit2.add(i); // add the current index to the ArrayList of checked array elements

                    // If lamp at current index has bases and that max number of bases needed has not been reached
                    if(table[i].getBase().equals("Y") && bases < number){
                        bCount = bases+1; // add base to the amount of bases found
                    }

                    // If lamp at current index has bulbs and that max number of bulbs needed has not been reached
                    if(table[i].getBulb().equals("Y") && lightBulbs < number){
                        lCount = lightBulbs+1; // add bulb to the amount of bulbs found
                    }
                    // adds the lamp price at current index to price sum and is saved for current recursion call
                    totalPrice2 = priceTotal + table[i].getPrice();
                    // if the amount bases + bulbs found is equal to max amount pieces needed to make order
                    if(bCount+lCount == number*2){
                        prices.add(totalPrice2); // add total price of combination to prices ArrayList
                        combinations.add(alreadyHit2); // add the indexes of the combined lamps to the combinations ArrayList
                        lowest = getLowestPrice(); // Update lowest lamp price
                        return lowest; // return updated lowest price
                    }
                    lowest = lampPrice(table, totalPrice2, alreadyHit2, type, number, bCount, lCount); // recursive call
                }
            }
        }
        return lowest; // return updated lowest price.
    }
}
