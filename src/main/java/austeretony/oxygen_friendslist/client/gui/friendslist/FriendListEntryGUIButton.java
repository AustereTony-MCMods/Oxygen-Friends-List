package austeretony.oxygen_friendslist.client.gui.friendslist;

import java.util.UUID;

import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.gui.IndexedGUIButton;
import austeretony.oxygen_core.client.gui.OxygenGUITextures;
import austeretony.oxygen_core.client.gui.elements.CustomRectUtils;
import austeretony.oxygen_core.client.gui.settings.GUISettings;
import austeretony.oxygen_core.common.PlayerSharedData;
import austeretony.oxygen_core.common.api.EnumDimension;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.util.OxygenUtils;
import austeretony.oxygen_core.server.OxygenPlayerData.EnumActivityStatus;
import austeretony.oxygen_friendslist.common.ListEntry;
import net.minecraft.client.renderer.GlStateManager;

public class FriendListEntryGUIButton extends IndexedGUIButton<UUID> {

    private String lastActivity, dimension, note;

    private int statusIconU;

    private boolean offline, hasNote;

    public FriendListEntryGUIButton(ListEntry entry) {
        super(entry.getPlayerUUID());
        PlayerSharedData sharedData = OxygenHelperClient.getPlayerSharedData(entry.getPlayerUUID());
        EnumActivityStatus activityStatus = EnumActivityStatus.values()[sharedData.getByte(OxygenMain.ACTIVITY_STATUS_SHARED_DATA_ID)];
        if (!OxygenHelperClient.isPlayerOnline(sharedData.getPlayerUUID()))
            activityStatus = EnumActivityStatus.OFFLINE;
        this.lastActivity = ClientReference.localize("oxygen.gui.lastActivityTime", OxygenUtils.getTimePassedLocalizedString(sharedData.getLastActivityTime()));
        this.setDisplayText(sharedData.getUsername());
        this.dimension = EnumDimension.getLocalizedNameFromId(sharedData.getInt(OxygenMain.DIMENSION_SHARED_DATA_ID));
        this.statusIconU = activityStatus.ordinal() * 3;
        this.offline = activityStatus == EnumActivityStatus.OFFLINE;
        this.hasNote = !entry.getNote().isEmpty();
        this.note = entry.getNote();
        this.setDynamicBackgroundColor(GUISettings.get().getEnabledElementColor(), GUISettings.get().getDisabledElementColor(), GUISettings.get().getHoveredElementColor());
        this.setTextDynamicColor(GUISettings.get().getEnabledTextColor(), GUISettings.get().getDisabledTextColor(), GUISettings.get().getHoveredTextColor());
        this.setTooltipScaleFactor(GUISettings.get().getTooltipScale());
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        if (this.isVisible()) {  
            GlStateManager.pushMatrix();           
            GlStateManager.translate(this.getX(), this.getY(), 0.0F);   

            int color, textColor, textY, noteIconU = 0;                      
            if (!this.isEnabled()) {                 
                color = this.getDisabledBackgroundColor();
                textColor = this.getDisabledTextColor();    
                noteIconU = 8;
            } else if (this.isHovered() || this.isToggled()) {                 
                color = this.getHoveredBackgroundColor();
                textColor = this.getHoveredTextColor();
                noteIconU = 16;
            } else {                   
                color = this.getEnabledBackgroundColor(); 
                textColor = this.getEnabledTextColor();      
            }

            int third = this.getWidth() / 3;
            CustomRectUtils.drawGradientRect(0.0D, 0.0D, third, this.getHeight(), 0x00000000, color, EnumGUIAlignment.RIGHT);
            drawRect(third, 0, this.getWidth() - third, this.getHeight(), color);
            CustomRectUtils.drawGradientRect(this.getWidth() - third, 0.0D, this.getWidth(), this.getHeight(), 0x00000000, color, EnumGUIAlignment.LEFT);

            textY = (this.getHeight() - this.textHeight(this.getTextScale())) / 2 + 1;

            GlStateManager.pushMatrix();           
            GlStateManager.translate(18.0F, textY, 0.0F); 
            GlStateManager.scale(this.getTextScale(), this.getTextScale(), 0.0F); 
            this.mc.fontRenderer.drawString(this.getDisplayText(), 0, 0, textColor, this.isTextShadowEnabled());
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();           
            GlStateManager.translate(100.0F, textY, 0.0F); 
            GlStateManager.scale(this.getTextScale(), this.getTextScale(), 0.0F);         
            this.mc.fontRenderer.drawString(this.dimension, 0, 0, textColor, this.isTextShadowEnabled());
            GlStateManager.popMatrix();

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);  

            this.mc.getTextureManager().bindTexture(OxygenGUITextures.STATUS_ICONS); 
            drawCustomSizedTexturedRect(7, 4, this.statusIconU, 0, 3, 3, 12, 3);  

            if (this.hasNote) {
                this.mc.getTextureManager().bindTexture(FriendsListGUIScreen.NOTE_ICONS); 
                drawCustomSizedTexturedRect(this.getWidth() - 20, 2, noteIconU, 0, 8, 8, 24, 8); 
            }

            GlStateManager.popMatrix();
        }
    }

    @Override
    public void drawTooltip(int mouseX, int mouseY) {
        if (this.offline && mouseX >= this.getX() + 7 && mouseY >= this.getY() + 4 && mouseX < this.getX() + 10 && mouseY < this.getY() + 7)
            this.drawAsTooltip(mouseX, mouseY, this.lastActivity);
        else if (this.hasNote && mouseX >= this.getX() + this.getWidth() - 20 && mouseY >= this.getY() + 2 && mouseX < this.getX() + this.getWidth() - 12 && mouseY < this.getY() + 8)
            this.drawAsTooltip(mouseX - this.textWidth(this.note, this.getTooltipScaleFactor()), mouseY, this.note);
    }

    private void drawAsTooltip(int mouseX, int mouseY, String text) {
        float scale = this.getTooltipScaleFactor();
        int 
        width = this.textWidth(text, scale) + 6,
        height = 12;
        GlStateManager.pushMatrix();           
        GlStateManager.translate(mouseX, mouseY - scale * height - 2, 0.0F);            
        GlStateManager.scale(scale, scale, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        //background
        drawRect(0, 0, width, height, GUISettings.get().getBaseGUIBackgroundColor());

        //frame
        CustomRectUtils.drawRect(0.0D, 0.0D, 0.4D, (double) height, GUISettings.get().getAdditionalGUIBackgroundColor());
        CustomRectUtils.drawRect((double) width - 0.4D, 0.0D, (double) width, (double) height, GUISettings.get().getAdditionalGUIBackgroundColor());
        CustomRectUtils.drawRect(0.0D, 0.0D, (double) width, 0.4D, GUISettings.get().getAdditionalGUIBackgroundColor());
        CustomRectUtils.drawRect(0.0D, (double) height - 0.4D, (double) width, (double) height, GUISettings.get().getAdditionalGUIBackgroundColor());

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.pushMatrix();           
        GlStateManager.translate((width - this.textWidth(text, scale)) / 2, (height - this.textHeight(scale)) / 2, 0.0F);            
        GlStateManager.scale(scale, scale, 0.0F);

        this.mc.fontRenderer.drawString(text, 0, 0, this.getEnabledTextColor(), false);

        GlStateManager.popMatrix();      

        GlStateManager.popMatrix(); 
    }
}
