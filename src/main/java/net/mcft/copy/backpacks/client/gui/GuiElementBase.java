package net.mcft.copy.backpacks.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiElementBase {
	
	public static final String ELLIPSIS = "...";
	public static final int ELLIPSIS_WIDTH = getFontRenderer().getStringWidth(ELLIPSIS);
	public static final int LINE_HEIGHT = getFontRenderer().FONT_HEIGHT;
	
	
	private GuiContext _context;
	private GuiContainer _parent;
	private int _width, _height;
	private Alignment _horizontalAlign = new Alignment.Min(0);
	private Alignment _verticalAlign = new Alignment.Min(0);
	
	
	void setContext(GuiContext value) { _context = value; }
	void setParent(GuiContainer value) { _parent = value; }
	
	public final GuiContext getContext() { return _context; }
	public final GuiContainer getParent() { return _parent; }
	
	// Size related
	
	public int getSize(Direction direction)
		{ return (direction == Direction.HORIZONTAL) ? _width : _height; }
	public final int getWidth() { return getSize(Direction.HORIZONTAL); }
	public final int getHeight() { return getSize(Direction.VERTICAL); }
	
	public void setSize(Direction direction, int value) {
		if (value == getSize(direction)) return;
		if (direction == Direction.HORIZONTAL) _width = value;
		else _height = value;
		onSizeChanged(direction);
		if (_parent != null) _parent.onChildSizeChanged(this, direction);
	}
	public final void setWidth(int value) { setSize(Direction.HORIZONTAL, value); }
	public final void setHeight(int value) { setSize(Direction.VERTICAL, value); }
	public final void setSize(int width, int height) { setWidth(width); setHeight(height); }
	
	// Alignment related
	
	public Alignment getAlign(Direction direction)
		{ return (direction == Direction.HORIZONTAL) ? _horizontalAlign : _verticalAlign; }
	public final Alignment getHorizontalAlign() { return getAlign(Direction.HORIZONTAL); }
	public final Alignment getVerticalAlign() { return getAlign(Direction.VERTICAL); }
	
	public void setAlign(Direction direction, Alignment value) {
		if (value == null) throw new NullPointerException("Argument must be non-null");
		if (direction == Direction.HORIZONTAL) _horizontalAlign = value;
		else _verticalAlign = value;
		if (_parent != null) _parent.onChildAlignChanged(this, direction);
	}
	public final void setHorizontalAlign(Alignment value) { setAlign(Direction.HORIZONTAL, value); }
	public final void setVerticalAlign(Alignment value) { setAlign(Direction.VERTICAL, value); }
	public final void setAlign(Alignment horizontal, Alignment vertical)
		{ setHorizontalAlign(horizontal); setVerticalAlign(vertical); }
	
	// Useful alignment shortcuts
	
	public final void setPosition(int left, int top)
		{ setAlign(new Alignment.Min(left), new Alignment.Min(top)); }
	
	public final void setLeft(int value) { setHorizontalAlign(new Alignment.Min(value)); }
	public final void setRight(int value) { setHorizontalAlign(new Alignment.Max(value)); }
	public final void setTop(int value) { setVerticalAlign(new Alignment.Min(value)); }
	public final void setBottom(int value) { setVerticalAlign(new Alignment.Max(value)); }
	
	public final void setLeftRight(int value) { setLeftRight(value, value); }
	public final void setLeftRight(int left, int right)
		{ setHorizontalAlign(new Alignment.Both(left, right)); }
	public final void setTopBottom(int value) { setTopBottom(value, value); }
	public final void setTopBottom(int top, int bottom)
		{ setVerticalAlign(new Alignment.Both(top, bottom)); }
	
	public final void setHorizontalCentered() { setHorizontalAlign(new Alignment.Center()); }
	public final void setHorizontalCentered(int width) { setHorizontalCentered(); setWidth(width); }
	public final void setVerticalCentered() { setVerticalAlign(new Alignment.Center()); }
	public final void setVerticalCentered(int height) { setVerticalCentered(); setHeight(height); }
	
	public final void setFill() { setFill(0); }
	public final void setFill(int padding) { setFill(padding, padding); }
	public final void setFill(int paddingHorizontal, int paddingVertical)
		{ setFill(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical); }
	public final void setFill(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom)
		{ setAlign(new Alignment.Both(paddingLeft, paddingRight),
		           new Alignment.Both(paddingTop, paddingBottom)); }
	
	// Control focus
	
	/** Returns if this control can be focused. */
	public boolean canFocus() { return false; }
	
	/** Returns if this control is currently focused. */
	public boolean isFocused() { return (getContext().getFocused() == this); }
	
	/** Makes this control the currently focused control. */
	public void setFocused() { setFocused(true); }
	/** Sets whether this control is the currently focused control. */
	public void setFocused(boolean value) {
		if (value && !canFocus())
			throw new UnsupportedOperationException("This control can't be focused");
		getContext().setFocused(value ? this : null);
	}
	
	// Drag related
	
	/** Returns if this control can be dragged. */
	public boolean canDrag() { return false; }
	
	/** Returns if this control is currently being pressed down. */
	public boolean isPressed() { return (getContext().getPressed() == this); }
	
	/** Returns if this control is currently being dragged. */
	public boolean isDragged() { return (canDrag() && isPressed()); }
	
	// Basic events
	
	/** Called when this control is added to a container. */
	public void onControlAdded() {  }
	
	/** Called when this control is resized. */
	public void onSizeChanged(Direction direction) {  }
	
	// Mouse events
	
	/** Called when the mouse is clicked in this control.
	 *  Mouse position is relative to the control's position.
	 *  Returns if the mouse action was handled. */
	public boolean onMouseDown(int mouseButton, int mouseX, int mouseY) {
		if (mouseButton == MouseButton.LEFT)
			getContext().setPressed(this);
		if (canFocus()) {
			setFocused();
			return true;
		} else return false;
	}
	
	/** Called when the mouse is moved over this control or being dragged.
	 *  Mouse position is relative to the control's position. */
	public void onMouseMove(int mouseX, int mouseY) {  }
	
	public void onMouseUp(int mouseButton, int mouseX, int mouseY) {  }
	
	// Keyboard events
	
	
	
	// Draw events
	
	/** Draws this control on the screen. Rendered relative to its position.
	 *  Mouse position is relative to the control's position. */
	public void draw(int mouseX, int mouseY, float partialTicks) {  }
	
	/** Draws this control's tooltip on the screen.
	 *  Tooltip is rendered relative to this control's position.
	 *  Mouse position is relative to the control's position. */
	public void drawTooltip(int mouseX, int mouseY, float partialTicks) {  }
	
	
	// Utility methods
	
	/** Returns whether the specified region (x, y, width, height)
	 *  contains the specified point (testX, testY). */
	public static boolean regionContains(int x, int y, int width, int height,
	                                     int testX, int testY) {
		return (testX >= x) && (testX < x + width) &&
		       (testY >= y) && (testY < y + height);
	}
	/** Returns whether the specified relative
	 *  position is within the control's region. */
	public boolean controlContains(int x, int y)
		{ return regionContains(0, 0, _width, _height, x, y); }
	
	public static Minecraft getMC() { return Minecraft.getMinecraft(); }
	public static FontRenderer getFontRenderer() { return getMC().fontRenderer; }
	public static void display(GuiScreen screen) { getMC().displayGuiScreen(screen); }
	
	// Utility classes
	
	public static final class MouseButton {
		
		private MouseButton() {  }
		
		public static int LEFT = 0;
		public static int RIGHT = 1;
		public static int MIDDLE = 2;
		
	}
	
}
