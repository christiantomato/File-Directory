/*
CS 1027B – Assignment 4
Name: Christian Tamayo
Student Number: 251 433 749
Email: ctamayo@uwo.ca
Created: Mar 29, 2025
Description: 
class defining a file system object (either a file or a folder)
*/


public class FileSystemObject implements Comparable<FileSystemObject> {
    //instance variables
    private String name;
    private OrderedListADT<FileSystemObject> children;
    private FileSystemObject parent;
    private int id;

    //constructor 
    public FileSystemObject(String name, int id) {
        this.name = name;
        this.id = id;
        //is it a file or folder? (check for extension - L.A)
        if(!this.name.contains(".")) {
            //then it is a folder, initialize the children in an ordered array list
            children = new ArrayOrderedList<FileSystemObject>();
        }
    }

    //getters

    public String getName() {
        return this.name;
    }

    public int getID() {
        return this.id;
    }

    public FileSystemObject getParent() {
        return this.parent;
    }

    public OrderedListADT<FileSystemObject> getChildren() {
        return this.children;
    }

    //setters

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(FileSystemObject parent) {
        this.parent = parent;
    }

    //check if it is a file or folder
    public boolean isFile() {
        //check for an extenstion
        return this.name.contains(".");
    }

    //helper method, used for checking for duplicate files in a folder
    private boolean childrenContains(String name) {
        //iterate through this and look for target
        for(FileSystemObject obj : this.getChildren()) {
            if(obj.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void addChild(FileSystemObject node) {
        //make sure its not a file
        if(this.isFile()) {
            throw new DirectoryTreeException("cannot store a file/folder within a file");
        }
        //before adding, check if there is a duplicate file
        if(this.childrenContains(node.getName())) {
            throw new DirectoryTreeException("folder already contains file/folder with provided name");
        }
        //if everything okay add the child
        this.children.add(node);
        //make sure to set this as the parent!
        node.setParent(this);
    }

    //toString
    public String toString() {
        //just return name of file or folder
        return this.name;
    }

    //helper method, used for determining size of a folder recursively
    private int computeFolderSize(FileSystemObject object) {
        //base case - object is a file
        if(object.isFile()) {
            //return the size of the file
            return ((ComputerFile) object).getSize();
        }
        else {
            //compute the size of all the children
            int sizeSubfolder = 0;
            for(FileSystemObject obj : object.getChildren()) {
                //compute the size of the children
                sizeSubfolder += computeFolderSize(obj);
            }
            //return size
            return sizeSubfolder;
        }
    }

    //clean code to get the size of the file object
    public int size() {
        //invoke the recursion here
        return computeFolderSize(this);
    }
    
    //implement the compareTo method, so we can order file objects
    public int compareTo(FileSystemObject other) {
        //folders come before files, so check all cases

        //if this a folder and other is a file
        if(!this.isFile() && other.isFile()) {
            //easy case
            return -1;
        }
        //if this a file and other is a folder
        else if(this.isFile() && !other.isFile()) {
            //also an easy case
            return 1;
        }
        else {
            //if they are either both a file or folder, sort alphabetically, use javas string compareToIgnoreCase
            return this.getName().compareToIgnoreCase(other.getName());
        }
    }
}
