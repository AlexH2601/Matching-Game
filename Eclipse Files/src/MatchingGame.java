/** Title: P02 Matching Game

 * Files: MatchingGame
 * Course: CS 300 ; Fall Semester ; 2019
 * 
 * @author Alex Huang
 * Email: anhuang@wisc.edu
 * Lecturer: Gary Dahl
 */
import java.io.File;
import java.util.Random;
import processing.core.PApplet;
import processing.core.PImage;

public class MatchingGame {
  
  //Congratulations message
  private final static String CONGRA_MSG = "CONGRATULATIONS! YOU WON!";
  
  //Cards not matched message
  private final static String NOT_MATCHED = "CARDS NOT MATCHED. Try again!";

  //Cards matched message
  private final static String MATCHED = "CARDS MATCHED! Good Job!";

  //2D-array which stores cards coordinates on the window display
  private final static float[][] CARDS_COORDINATES =
  new float[][] {{170, 170}, {324, 170}, {478, 170}, {632, 170},
    {170, 324}, {324, 324}, {478, 324}, {632, 324},
    {170, 478}, {324, 478}, {478, 478}, {632, 478}};

  //Array that stores the card images filenames
  private final static String[] CARD_IMAGES_NAMES = new String[] {"apple.png",
    "ball.png", "peach.png", "redFlower.png", "shark.png", "yellowFlower.png"};
  
  private static PApplet processing; // PApplet object that represents
  //the graphic display window
  private static Card[] cards; // one dimensional array of cards
  private static PImage[] images; // array of images of the different cards
  private static Random randGen; // generator of random numbers
  private static Card selectedCard1; // First selected card
  private static Card selectedCard2; // Second selected card
  private static boolean winner; // boolean evaluated true if the game is won,
  //and false otherwise
  private static int matchedCardsCount; // number of cards matched so far
  //in one session of the game
  private static String message; // Displayed message to the display window
  
  public static void main (String[] args) {
    Utility.runApplication();
  }
  
  /**
  * Defines the initial environment properties of this game as the program starts
  */
  public static void setup(PApplet processing) {
    // Set the color used for the background of the Processing window
    MatchingGame.processing = processing;
   
    // Initializing the images array static field
    images = new PImage[MatchingGame.CARD_IMAGES_NAMES.length];
    
    // Loading the image files as PImage objects and storing into images[]
    for (int i = 0 ; i < images.length ; i++) {
      images[i] = processing.loadImage("images" + File.separator + CARD_IMAGES_NAMES[i]);
    }
    
    // Starting the game
    initGame();
  }
  
  /**
  * Initializes the Game
  */
  public static void initGame() {
    // Initializing static fields 
    randGen = new Random(Utility.getSeed());
    selectedCard1 = null;
    selectedCard2 = null;
    matchedCardsCount = 0;
    winner = false;
    message = "";
    cards = new Card[CARD_IMAGES_NAMES.length*2];
    
    // Index of the specific iteration's randomly selected image
    int currRandImage;
    // Index of the specific iteration's randomly selected coordinate pair
    int currRandCoord;      
    // Total number of coordinate pairs in CARDS_COORDINATES[]
    int numCoordinates = CARDS_COORDINATES.length;
    // Total number of images in images[]
    int numImages = images.length;
    // PImage of the specific iteration's randomly selected image
    PImage currImage;
    // Parallel array to images[] that track how many times an image is used
    int[] usedImages = new int[numImages];
    // Parallel array to CARDS_COORDINATES[] that tracks how many times a coord pair is used
    int[] usedCoordinates = new int[numCoordinates];
    
    // For loop that runs through each card and determines image and location
    for (int i = 0 ; i < cards.length ; i++) {      
      // While loop that randomly selects an available image to use
      while (true) {
        // Randomly selected images[] index
        currRandImage = randGen.nextInt(numImages);
        // Verifies that the selected image has not already appeared twice
        if (usedImages[currRandImage] < 2) {
          currImage = images[currRandImage];
          usedImages[currRandImage]++;
          break;
        }
      }
      // While loop that randomly selects an available coordinate pair to use
      while (true) {
        // Randomly selected CARDS_COORDINATES[] index
        currRandCoord = randGen.nextInt(numCoordinates);
        // Verifies that the selected coord pair has not already been used
        if (usedCoordinates[currRandCoord] != 1) {
          usedCoordinates[currRandCoord]++;
          break;
        }
      }
      // Fills the current index of cards[] with the randomized card
      cards[i] = new Card(currImage, CARDS_COORDINATES[currRandCoord][0], 
          CARDS_COORDINATES[currRandCoord][1]);
    }
  }
  
  /**
  * Callback method called each time the user presses a key
  */
  public static void keyPressed() {
    // If the player presses 'N', the game resets
    if (processing.key == ('N') || processing.key == ('n')) {
      initGame();
    }
  }
  
  /**
  * Callback method draws continuously this application window display
  */
  public static void draw() {
    processing.background(245, 255, 250); // Mint cream background color
    
    // Drawing all cards of cards[]
    for (int i = 0 ; i < cards.length ; i++) {
        cards[i].draw();
    }
    
    displayMessage(message);
  }
  
  /**
  * Displays a given message to the display window
  * @param message to be displayed to the display window
  */
  public static void displayMessage(String message) {
    processing.fill(0);
    processing.textSize(20);
    processing.text(message, processing.width / 2, 50);
    processing.textSize(12);
  }
  
  /**
  * Checks whether the mouse is over a given Card
  * @return true if the mouse is over the storage list, false otherwise
  */
  public static boolean isMouseOver(Card card) {
    float cardHeight = card.getHeight();
    float cardWidth = card.getWidth();
    float xCenter = card.getX();
    float yCenter = card.getY();
    float mouseXF = processing.mouseX;
    float mouseYF = processing.mouseY;
    // Calculates right X bound of card
    float xRPosition = xCenter + (cardWidth / 2);
    // Calculates left X bound of card
    float xLPosition = xCenter - (cardWidth / 2);
    // Calculates upper Y bound of card
    float yUPosition = yCenter + (cardHeight / 2);
    // Calculates lower Y bound of card
    float yLPosition = yCenter - (cardHeight / 2);
    
    // Checks if the mouse cursor is between the X and Y bounds of the card (over the card)
    if (mouseXF > xLPosition && mouseXF < xRPosition && mouseYF > yLPosition && 
        mouseYF < yUPosition) {
      return true;
    }
    else {
      return false;
    }
  }
  
  /**
  * Callback method called each time the user presses the mouse
  */
  public static void mousePressed() {
    // Mouse presses only checked if the game is not over
    if (!winner) {
      // Makes sure selectedCard1 and selectedCard2 have values (to avoid null exception)
      if (selectedCard1 != null && selectedCard2 != null) {
        // Checks if selectedCard1 and selectedCard2 match and empties the two static fields
        if (matchingCards(selectedCard1, selectedCard2)) {
          selectedCard1.deselect();
          selectedCard2.deselect();
          selectedCard1 = null;
          selectedCard2 = null;
        }
        else {
          selectedCard1.setVisible(false);
          selectedCard1.deselect();
          selectedCard2.setVisible(false);
          selectedCard2.deselect();
          selectedCard1 = null;
          selectedCard2 = null;
        }
      }
      // Checks if the mouse is over any card
      for (Card c : cards) {
        if (isMouseOver(c)) {
          // Verifies that the chosen card isn't already flipped over
          if (!c.isVisible()) {
            if (selectedCard1 == null) {
              selectedCard1 = c;
              selectedCard1.setVisible(true);
              selectedCard1.select();
              message = "";
            }
            // Verifies that the chosen card is not the same as selectedCard1
            else if (c.getX() != selectedCard1.getX() || c.getY() != selectedCard1.getY()) {
              selectedCard2 = c;
              selectedCard2.setVisible(true);
              selectedCard2.select();
            }
          } 
        }
      }
      // Checks if selectedCard1 and selectedCard2 match and displays the correct message
      if (selectedCard1 != null && selectedCard2 != null) {
        if (matchingCards(selectedCard1, selectedCard2)) {
          matchedCardsCount = matchedCardsCount + 2;
          message = MATCHED;
        }
        else {
          message = NOT_MATCHED; 
        }
      }
      // Checks if the player has matched all the cards
      if (matchedCardsCount == 12) {
        selectedCard1.deselect();
        selectedCard2.deselect();
        winner = true;
        message = CONGRA_MSG;
      }
    }
  }
  
  /**
  * Checks whether two cards match or not
  * @param card1 reference to the first card
  * @param card2 reference to the second card
  * @return true if card1 and card2 image references are the same, false otherwise
  */
  public static boolean matchingCards(Card card1, Card card2) {
    // Compares the card1 and card2 image references
    if (card1.getImage().equals(card2.getImage())) {
      return true;
    }
    else {
      return false;
    }
  }
}