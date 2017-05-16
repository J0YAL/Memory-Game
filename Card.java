import javax.swing.JButton;
import javax.swing.ImageIcon;

/**
     A card has different images on both sides.
     @author ZIWEN LI
     @version 1.0
*/
public class Card extends JButton
{
	//Location of the image for the face of the card
	private String imagePath;
	
	//By default, a card shows its back
	private boolean faceUp;
	
	//The icon that displays on the card, when the card faces up
	private ImageIcon image;
	
	//The icon that displays on the card, when the card faces down
	private ImageIcon cardback;
   
   //A card holds a number
   private int cardNumber;
	
   /**
        Creates a card with its face down by default.
   */
   public Card()
   {
      super();
      setCardBack();
      this.setIcon(cardback);
      cardNumber = -1;
   }
   
   /**
        Creates an image for the back of the card.
   */
   public void setCardBack()
   {
      cardback = new ImageIcon("images/back.jpg");
   }
   
   /**
        Creates an image for the face of the card.
        @param the location of the image
   */
   public void setImage(String imagePath)
   {
      image = new ImageIcon(imagePath);
   }
   
   /**
        Sets the card number.
        @param cardNumber a card number
   */
   public void setCardNumber(int cardNumber)
   {
      this.cardNumber = cardNumber;
   }
   
   /**
        Gets the card number.
        @return the card number
   */
   public int getCardNumber()
   {
      return cardNumber;
   }
	
	/**
	    Flips the card face up to display the image.
    */
	public void reveal()
	{
      if (!faceUp)
      {
         setIcon(image);
         faceUp = true;
      }
	}
   
    /**
	    Flips the card face down to display the card back.
    */
	public void cover()
	{
      setIcon(cardback);
      faceUp = false;
	}
   
	/**
        Flips the card.
	    @param face if true, card faces up. Otherwise, faces down
    */
	public void setFaceUp(boolean face)
	{
		faceUp = face;
	}
	
	/**
	    Gets the face of the card.
	    @return true = card face image; false = card back image
    */
	public boolean isFaceUp()
	{
		return faceUp;
	}
	
}