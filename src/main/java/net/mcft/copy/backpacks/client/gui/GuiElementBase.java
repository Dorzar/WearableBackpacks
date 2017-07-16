package net.mcft.copy.backpacks.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiElementBase {
	
	public static final String ELLIPSIS = "...";
	public static final int ELLIPSIS_WIDTH = getStringWidth(ELLIPSIS);
	public static final int LINE_HEIGHT = getFontRenderer().FONT_HEIGHT;
	
	public static final int COLOR_CONTROL = 0xFFE0E0E0;
	public static final int COLOR_CONTROL_HIGHLIGHT = 0xFFFFFFA0;
	public static final int COLOR_CONTROL_DISABLED = 0xFFA0A0A0;
	
	
	private GuiContext _context;
	private GuiContainer _parent;
	private int _width, _height;
	private boolean _visible = true, _enabled = true;
	private Alignment _horizontalAlign = new Alignment.Min(0);
	private Alignment _verticalAlign = new Alignment.Min(0);
	
	
	void setContext(GuiContext value) { _context = value; }
	void setParent(GuiContainer value) { _parent = value; }
	
	public final GuiContext getContext() { return _context; }
	public final GuiContainer getParent() { return _parent; }
	
	public final boolean isVisible() { return _visible; }
	public final void setVisible(boolean value) { _visible = value; }
	
	public boolean isEnabled() { return _enabled &&
		((getParent() != null) ? getParent().isEnabled() : true); }
	public void setEnabled(boolean value) { _enabled = value; }
	
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
	public final void setAlignHorizontal(Alignment value) { setAlign(Direction.HORIZONTAL, value); }
	public final void setAlignVertical(Alignment value) { setAlign(Direction.VERTICAL, value); }
	public final void setAlign(Alignment horizontal, Alignment vertical)
		{ setAlignHorizontal(horizontal); setAlignVertical(vertical); }
	
	// Useful alignment shortcuts
	
	public final void setPosition(int left, int top)
		{ setAlign(new Alignment.Min(left), new Alignment.Min(top)); }
	
	public final void setLeft(int value) { setAlignHorizontal(new Alignment.Min(value)); }
	public final void setRight(int value) { setAlignHorizontal(new Alignment.Max(value)); }
	public final void setTop(int value) { setAlignVertical(new Alignment.Min(value)); }
	public final void setBottom(int value) { setAlignVertical(new Alignment.Max(value)); }
	
	public final void setLeftRight(int value) { setLeftRight(value, value); }
	public final void setLeftRight(int left, int right)
		{ setAlignHorizontal(new Alignment.Both(left, right)); }
	public final void setTopBottom(int value) { setTopBottom(value, value); }
	public final void setTopBottom(int top, int bottom)
		{ setAlignVertical(new Alignment.Both(top, bottom)); }
	
	public final void setCenteredHorizontal() { setAlignHorizontal(new Alignment.Center()); }
	public final void setCenteredHorizontal(int width) { setCenteredHorizontal(); setWidth(width); }
	public final void setCenteredVertical() { setAlignVertical(new Alignment.Center()); }
	public final void setCenteredVertical(int height) { setCenteredVertical(); setHeight(height); }
	
	public final void setFillHorizontal() { setLeftRight(0); }
	public final void setFillVertical() { setTopBottom(0); }
	public final void setFill() { setFillHorizontal(); setFillVertical(); }
	
	// Element focus
	
	/** Returns if this element can be focused. */
	public boolean canFocus() { return false; }
	
	/** Returns if this element is currently focused. */
	public boolean isFocused() { return (getContext().getFocused() == this); }
	
	/** Makes this element the currently focused element. */
	public void setFocused() { setFocused(true); }
	/** Sets whether this element is the currently focused element. */
	public void setFocused(boolean value) {
		if (value && !canFocus())
			throw new UnsupportedOperationException("This element can't be focused");
		if (!isVisible() || !isEnabled()) return;
		getContext().setFocused(value ? this : null);
	}
	
	// Drag related
	
	/** Returns if this element can be dragged. */
	public boolean canDrag() { return false; }
	
	/** Returns if this element is currently being pressed down. */
	public boolean isPressed() { return (getContext().getPressed() == this); }
	
	/** Returns if this element is currently being dragged. */
	public boolean isDragged() { return (canDrag() && isPressed()); }
	
	// Basic events
	
	/** Called when this element is resized. */
	public void onSizeChanged(Direction direction) {  }
	
	// Mouse events
	
	/** Called when the mouse is clicked in this element.
	 *  Mouse position is relative to the element's position.
	 *  Returns if the mouse action was handled. */
	public boolean onMouseDown(int mouseButton, int mouseX, int mouseY) {
		if (!isVisible()) return false;
		if (mouseButton == MouseButton.LEFT)
			getContext().setPressed(this);
		if (canFocus()) {
			setFocused();
			return true;
		} else return false;
	}
	
	/** Called when the mouse is moved over this element or being dragged.
	 *  Mouse position is relative to the element's position. */
	public void onMouseMove(int mouseX, int mouseY) {  }
	
	public void onMouseUp(int mouseButton, int mouseX, int mouseY) {  }
	
	// Keyboard events
	
	
	
	// Draw events
	
	/** Draws this element on the screen. Rendered relative to its position.
	 *  Mouse position is relative to the element's position. */
	public void draw(int mouseX, int mouseY, float partialTicks) {  }
	
	/** Draws this element's tooltip on the screen.
	 *  Tooltip is rendered relative to this element's position.
	 *  Mouse position is relative to the element's position. */
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
	 *  position is within the element's region. */
	public boolean contains(int x, int y)
		{ return regionContains(0, 0, getWidth(), getHeight(), x, y); }
	
		
	public static Minecraft getMC() { return Minecraft.getMinecraft(); }
	public static void display(GuiScreen screen) { getMC().displayGuiScreen(screen); }
	
	public static FontRenderer getFontRenderer() { return getMC().fontRenderer; }
	public static int getStringWidth(String text) { return getFontRenderer().getStringWidth(text); }
	
	public static void bindTexture(ResourceLocation resource)
		{ getMC().getTextureManager().bindTexture(resource); }
	
	public static void setRenderColor(float red, float green, float blue)
		{ setRenderColor(red, green, blue, 1.0F); }
	public static void setRenderColor(float red, float green, float blue, float alpha)
		{ GlStateManager.color(red, green, blue, alpha); }
	
	public static void setRenderColorRGB(int color)
		{ setRenderColorARGB(color | 0xFF000000); }
	public static void setRenderColorARGB(int color) {
		setRenderColor((color >> 16 & 0xFF) / 255.0f,
		               (color >> 8 & 0xFF) / 255.0f,
		               (color & 0xFF) / 255.0f,
		               (color >> 24 & 0xFF) / 255.0f); }
	
	// Utility classes
	
	public static final class MouseButton {
		private MouseButton() {  }
		
		public static int LEFT   = 0;
		public static int RIGHT  = 1;
		public static int MIDDLE = 2;
	}
	
	public static final class Color {
		private Color() {  }
		
		public static int WHITE       = 0xFFFFFFFF;
		public static int BLACK       = 0xFF000000;
		public static int TRANSPARENT = 0x00000000;
	}
	
}
