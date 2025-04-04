/*
CS 1027B – Assignment 4
Name: Christian Tamayo
Student Number: 251 433 749
Email: ctamayo@uwo.ca
Created: Mar 30, 2025
Description: 
class defining a file object, which extends from the file system object definition
*/

public class ComputerFile extends FileSystemObject {
    //instance variables
    private int size; 

    //constructor
    public ComputerFile(String name, int id, int size) {
        //call to super
        super(name, id);
        //initialize the new size variables
        this.size = size;
    }

    //one getter just to return the size
    public int getSize() {
        return this.size;
    }
}
