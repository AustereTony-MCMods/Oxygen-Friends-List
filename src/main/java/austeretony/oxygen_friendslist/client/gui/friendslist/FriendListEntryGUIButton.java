package austeretony.oxygen_friendslist.client.gui.friendslist;

import austeretony.alternateui.screen.image.GUIImageLabel;
import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.OxygenGUITextures;
import austeretony.oxygen.client.gui.PlayerGUIButton;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen.common.api.EnumDimension;
import austeretony.oxygen.common.main.OxygenMain;
import austeretony.oxygen.common.main.OxygenPlayerData;
import austeretony.oxygen.common.main.SharedPlayerData;
import austeretony.oxygen_friendslist.common.main.FriendListEntry;
import net.minecraft.client.renderer.GlStateManager;

public class FriendListEntryGUIButton extends PlayerGUIButton {

    public final FriendListEntry listEntry;

    private String dimension, lastActivity;

    private int statusIconU;

    private GUIImageLabel statusImageLabel, noteImageLabel;

    private boolean hasNote;

    public FriendListEntryGUIButton(FriendListEntry listEntry, OxygenPlayerData.EnumActivityStatus status) {
        super(listEntry.playerUUID);
        this.listEntry = listEntry;
        SharedPlayerData sharedData;
        if (status != OxygenPlayerData.EnumActivityStatus.OFFLINE) {
            this.setDisplayText(OxygenHelperClient.getSharedPlayerData(listEntry.playerUUID).getUsername());//need for search mechanic
            this.dimension = EnumDimension.getLocalizedNameFromId(OxygenHelperClient.getPlayerDimension(listEntry.playerUUID));
        } else {
            sharedData = OxygenHelperClient.getObservedSharedData(listEntry.playerUUID);
            this.setDisplayText(sharedData.getUsername());//need for search mechanic
            this.dimension = EnumDimension.getLocalizedNameFromId(sharedData.getByte(OxygenMain.DIMENSION_SHARED_DATA_ID));
            if (sharedData.getLastActivityTime() > 0L) {
                int mode = 0;
                long 
                diff = System.currentTimeMillis() - sharedData.getLastActivityTime(),
                hours = diff / 86_400_000L,
                days;
                if (hours >= 24L)
                    mode = 1;               
                if (mode == 0) {
                    if (hours % 10L == 1L)
                        this.lastActivity = ClientReference.localize("oxygen.lastActivity.hour", hours);
                    else
                        this.lastActivity = ClientReference.localize("oxygen.lastActivity.hours", hours);
                } else {
                    days = hours / 24L;
                    if (days % 10L == 1L)
                        this.lastActivity = ClientReference.localize("oxygen.lastActivity.day", days);
                    else               
                        this.lastActivity = ClientReference.localize("oxygen.lastActivity.days", days);
                }
            } else
                this.lastActivity = ClientReference.localize("oxygen.lastActivity.noData");
        }
        this.statusIconU = status.ordinal() * 3;
        this.hasNote = !listEntry.getNote().isEmpty();
    }

    @Override
    public void init() {
        this.statusImageLabel = new GUIImageLabel(7, 3, 3, 3).setTexture(OxygenGUITextures.STATUS_ICONS, 3, 3, this.statusIconU, 0, 12, 3).initScreen(this.getScreen());
        this.statusImageLabel.setTextureUV(this.statusIconU, 0);
        if (this.lastActivity != null)
            this.statusImageLabel.initSimpleTooltip(this.lastActivity, GUISettings.instance().getTooltipScale());
        this.noteImageLabel = new GUIImageLabel(this.getWidth() - 20, 1, 8, 8).setTexture(OxygenGUITextures.NOTE_ICONS, 8, 8, 0, 0, 24, 8).initScreen(this.getScreen()).initSimpleTooltip(this.listEntry.getNote(), GUISettings.instance().getTooltipScale());
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        if (this.isVisible()) {  
            GlStateManager.pushMatrix();           
            GlStateManager.translate(this.getX(), this.getY(), 0.0F);        
            int color, textColor, textY;                      
            if (!this.isEnabled()) {                 
                color = this.getDisabledBackgroundColor();
                textColor = this.getDisabledTextColor();           
            } else if (this.isHovered() || this.isToggled()) {                 
                color = this.getHoveredBackgroundColor();
                textColor = this.getHoveredTextColor();
            } else {                   
                color = this.getEnabledBackgroundColor(); 
                textColor = this.getEnabledTextColor();      
            }
            this.drawRect(0, 0, this.getWidth(), this.getHeight(), color);
            textY = (this.getHeight() - this.textHeight(this.getTextScale())) / 2 + 1;
            GlStateManager.pushMatrix();           
            GlStateManager.translate(24.0F, textY, 0.0F); 
            GlStateManager.scale(this.getTextScale(), this.getTextScale(), 0.0F); 
            this.mc.fontRenderer.drawString(this.getDisplayText(), 0, 0, textColor, this.isTextShadowEnabled());
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();           
            GlStateManager.translate(100.0F, textY, 0.0F); 
            GlStateManager.scale(this.getTextScale(), this.getTextScale(), 0.0F);         
            this.mc.fontRenderer.drawString(this.dimension, 0, 0, textColor, this.isTextShadowEnabled());
            GlStateManager.popMatrix();
            this.statusImageLabel.draw(mouseX, mouseY);
            if (this.hasNote)
                this.noteImageLabel.draw(mouseX, mouseY);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void drawTooltip(int mouseX, int mouseY) {
        this.statusImageLabel.drawTooltip(mouseX, mouseY);
        if (this.hasNote)
            this.noteImageLabel.drawTooltip(mouseX, mouseY);
    }

    @Override
    public void mouseOver(int mouseX, int mouseY) {
        this.statusImageLabel.mouseOver(mouseX - this.getX(), mouseY - this.getY());
        if (this.hasNote)
            this.noteImageLabel.mouseOver(mouseX - this.getX(), mouseY - this.getY());
        this.setHovered(this.isEnabled() && mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + (int) (this.getWidth() * this.getScale()) && mouseY < this.getY() + (int) (this.getHeight() * this.getScale()));   
    }
}
