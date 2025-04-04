/*
CS 1027B – Assignment 4
Name: Christian Tamayo
Student Number: 251 433 749
Email: ctamayo@uwo.ca
Created: Mar 29, 2025
Description: 
class defining a file system directory, represented as a tree structure, with file system objects as nodes
*/

public class DirectoryTree {
    //instance variables
    private FileSystemObject root;

    //constructor
    public DirectoryTree(FileSystemObject rt) {
        //initialize the root I guess
        this.root = rt;
    }

    //get root
    public FileSystemObject getRoot() {
        return this.root;
    }

    //find the level of the given folder/file
    public int level(FileSystemObject fso) {
        //use recursion to simplify problem, base case is at root
        if(fso == this.root) {
            //level is 0
            return 0;
        }
        return level(fso.getParent()) + 1;
    }

    //helper method for determining the lowest common ancestor between 2 file system objects
    private FileSystemObject findLowestCommonAncestor(FileSystemObject a, FileSystemObject b) {
        //go up the ancestor chain from node at deepest level to level of higher node
        if(level(a) > level(b)) {
            FileSystemObject x = a.getParent();
            while(level(x) > level(b)) {
                //keep going up the tree
                x = x.getParent();
            }
            //now that they are on the same level check for same parents and do recursive calls and stuff
            //also make sure that they are not the same!!!
            if(x.compareTo(b) == 0) {
                //then b is the parent actually
                return b;
            }
            if(x.getParent().compareTo(b.getParent()) == 0) {
                return x.getParent();
            }
            else {
                return findLowestCommonAncestor(x.getParent(), b.getParent());
            }
        }
        else if(level(b) > level(a)) {
            //same algorithm just go up from b
            FileSystemObject x = b.getParent();
            while(level(x) > level(a)) {
                x = x.getParent();
            }
            //make sure they are not the same
            if(x.compareTo(a) == 0) {
                //then a is the parent 
                return a;
            }
            if(x.getParent().compareTo(a.getParent()) == 0) {
                return x.getParent();
            }
            else {
                return findLowestCommonAncestor(x.getParent(), a.getParent());
            }
        }
        //if they are already at the same level then we don't have to do all that
        //check to see if parents match - once we do, that is the base case
        if(a.getParent().compareTo(b.getParent()) == 0) {
            //return the ancestor
            return a.getParent();
        }
        else {
            //recursive call for parents
            return findLowestCommonAncestor(a.getParent(), b.getParent());
        }
    }

    //find the lowest common ancenstor, using the recursive helper method
    public FileSystemObject lca(FileSystemObject a, FileSystemObject b) {
        //invoke the recursion helper method here
        return findLowestCommonAncestor(a, b);
    }

    //helper method for traversing down a subtree and finding path to target
    private String pathDownTo(FileSystemObject top, FileSystemObject target) {
        String pathDown = "";
        //initially implemented using an array list but that wasn't allowed, 
        //so instead I have inefficiently created an array with arbitrarily long size resulting in this inefficient implementation.
        String[] parents = new String[100];
        //also need to include the target in this path for some reason
        parents[0] = target.getName();
        //since top is where we need to get to, start from target and list out the path until we get to top
        int i = 0;
        while(target.getParent().compareTo(top) != 0) {
            i++;
            //add to the list of traversals we going through
            parents[i] = target.getParent().getName();
            //go up
            target = target.getParent();
        }
        //once thats done list out the path in reverse order from what we inputted
        while(i >= 0) {
            pathDown += parents[i];
            if(i > 0) {
                pathDown += "/";
            }
            i--;
        }
        //then return the string we created
        return pathDown;
    }

    public String buildPath(FileSystemObject a, FileSystemObject b) {
        String path = "";
        //if they are in the same folder, simply return name of b
        if(a.getParent().compareTo(b.getParent()) == 0) {
            return b.getName();
        }
        else {
            //find the lowest common ancestor
            FileSystemObject lca = findLowestCommonAncestor(a, b);
            //how far from a is it? 
            int distanceUp = level(a) - level(lca);
            for(int i = 0; i < distanceUp; i++) {
                //build the path for going up the tree
                path += "../";
            }
            //now we have to go back down to b (if required)
            path += pathDownTo(lca, b);
        }
        //return the path we built
        return path;
    }

    //helper method to perform the preorder traversal for the toString, indent is initally 0
    private String preOrderGetString(FileSystemObject node, String indent) {
        //the string we will return 
        String s = "";
        //make sure the current node is not null
        if(node == null) {
            //this is the base case, return empty string
            return "";
        }
        else {
            //since it is preorder, do stuff now
            //if on root node
            if(node.compareTo(this.root) == 0) {
                //no special stuff
                s += node.getName() + "\n";
            }
            else {
                //space, hyphen, space
                s += indent + " - " + node.getName() + "\n";
            }
            //go to children (if it is a folder)
            if(!node.isFile()) {
                //start increasing indent after first line
                if(node.compareTo(this.root) != 0) {
                    //increase the indent since we are going to go a level down
                    indent += "  ";
                }
                for (FileSystemObject child : node.getChildren()) {
                    //preorder visit each child
                    s += preOrderGetString(child, indent);
                }
            }
        }
        //after done everything return the string we created
        return s;
    }

    //toString
    public String toString() {
        //invoking the recursion
        String directoryAsString = preOrderGetString(this.root, "");
        //returning the result
        return directoryAsString;
    }

    public void cutPaste(FileSystemObject f, FileSystemObject dest) {
        //make sure destination is not a file
        if(dest.isFile()) {
            throw new DirectoryTreeException("cannot store in a file");
        }
        //make sure we are not moving the root
        if(f.compareTo(this.root) == 0) {
            throw new DirectoryTreeException("cannot move the root of directory");
        }
        //move the file system object to the desired folder, ensuring good connections
        FileSystemObject parent = f.getParent();   
        //estrange the child
        parent.getChildren().remove(f);
        //destination adopt f
        dest.addChild(f);
        //accept new parent
        f.setParent(dest);
    }

    //helper method to clone a tree object
    private FileSystemObject cloneDirectory(FileSystemObject node) {
        //going to do this recursively, base case is if we reached a file (no children)
        if(node.isFile()) {
            //make sure to clone size for file as well
            ComputerFile cloneFile = new ComputerFile(node.getName(), node.getID() + 100, ((ComputerFile) node).getSize());
            //return here cause no more children
            return cloneFile;
        }
        //clone a folder
        FileSystemObject cloneFolder = new FileSystemObject(node.getName(), node.getID() + 100);
        //clone the children
        for (FileSystemObject child : node.getChildren()) {
            //recursively clone the children and add them to parent
            cloneFolder.addChild(cloneDirectory(child));
        }
        //return the folder once done
        return cloneFolder;
        
    }

    public void copyPaste(FileSystemObject f, FileSystemObject dest) {
        //make sure destination is not a file
        if(dest.isFile()) {
            throw new DirectoryTreeException("cannot store in a file");
        }
        //add the clone to the destintation directory
        dest.getChildren().add(cloneDirectory(f));
    }
}
