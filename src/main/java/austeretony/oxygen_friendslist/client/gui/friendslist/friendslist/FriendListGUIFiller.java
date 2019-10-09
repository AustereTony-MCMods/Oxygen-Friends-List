package austeretony.oxygen_friendslist.client.gui.friendslist.friendslist;

import austeretony.oxygen_core.client.gui.elements.BackgroundGUIFiller;
import austeretony.oxygen_core.client.gui.elements.CustomRectUtils;

public class FriendListGUIFiller extends BackgroundGUIFiller {

    public FriendListGUIFiller(int xPosition, int yPosition, int width, int height) {             
        super(xPosition, yPosition, width, height);
    }

    @Override
    public void drawBackground() {
        //main background  
        drawRect(0, 0, this.getWidth(), this.getHeight(), this.getEnabledBackgroundColor());      

        //title underline
        CustomRectUtils.drawRect(4.0D, 14.0D, this.getWidth() - 4.0D, 14.4D, this.getDisabledBackgroundColor());
        
        CustomRectUtils.drawRect(4.0D, 165.0D, this.getWidth() - 4.0D, 165.4D, this.getDisabledBackgroundColor());
    }
}
