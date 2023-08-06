package unsafedodo.fabricauctionhouse.gui;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementBuilderInterface;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import unsafedodo.fabricauctionhouse.AuctionHouseMain;
import unsafedodo.fabricauctionhouse.auction.AuctionHouse;
import unsafedodo.fabricauctionhouse.auction.AuctionItem;
import unsafedodo.fabricauctionhouse.config.ConfigManager;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class GUIPersonalAuctionHouse extends SimpleGui {

    public static final int PAGE_SIZE = 36; //9x4
    protected int page = 0;
    private int ticker = 0;

    public static int maxPageNumber;

    static {
        try {
            maxPageNumber = ConfigManager.getConfigData(ConfigManager.configFile).getAuctionHouseMaxPages();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    private AuctionHouse ah;

    public <T extends GUIPersonalAuctionHouse> GUIPersonalAuctionHouse(ServerPlayerEntity player) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);
        ah = AuctionHouseMain.ah.getPlayerAuctionHouse(player.getUuidAsString());
    }

    protected void nextPage() {
        this.page = Math.min(this.getPageAmount() - 1, this.page + 1);
        this.updateDisplay();
    }

    protected boolean canNextPage() {
        return this.getPageAmount() > this.page + 1;
    }

    protected void previousPage() {
        this.page = Math.max(0, this.page - 1);
        this.updateDisplay();
    }

    protected boolean canPreviousPage() {
        return this.page - 1 >= 0;
    }

    public void updateDisplay() {
        for (int i = 0; i < PAGE_SIZE; i++) {
            var element = this.getElement(i);

            if (element == null) {
                element = DisplayElement.empty();
            }

            if (element.element() != null) {
                this.setSlot(i, element.element());
            } else if (element.slot() != null) {
                this.setSlotRedirect(i, element.slot());
            }
        }

        for (int i = 0; i < 9; i++) {
            var navElement = this.getNavElement(i);

            if (navElement == null) {
                navElement = DisplayElement.EMPTY;
            }

            if (navElement.element != null) {
                this.setSlot(i + PAGE_SIZE, navElement.element);
            } else if (navElement.slot != null) {
                this.setSlotRedirect(i + PAGE_SIZE, navElement.slot);
            }
        }
    }

    protected DisplayElement getNavElement(int id) {
        return switch (id) {
            case 0 -> DisplayElement.of(
                    new GuiElementBuilder(Items.RED_CONCRETE)
                            .setName(Text.literal("Back").formatted(Formatting.RED))
                            .hideFlags()
                            .setCallback((x, y, z) -> {
                                playClickSound(this.player);
                                try {
                                    openPublic();
                                } catch (FileNotFoundException | UnsupportedEncodingException e) {
                                    throw new RuntimeException(e);
                                }
                            }));
            case 3 -> DisplayElement.previousPage(this);
            case 4 -> DisplayElement.of(
                    new GuiElementBuilder(Items.BARRIER)
                            .setName(Text.translatable("spectatorMenu.close").formatted(Formatting.RED))
                            .hideFlags()
                            .setCallback((x, y, z) -> {
                                playClickSound(this.player);
                                this.close();
                            })
            );
            case 5 -> DisplayElement.nextPage(this);
            case 8 -> DisplayElement.of(
                    new GuiElementBuilder(Items.HOPPER)
                            .setName(Text.literal("Expired Items").formatted(Formatting.RED))
                            .hideFlags()
                            .setCallback((x, y, z) -> {
                                playClickSound(this.player);
                                this.openExpiredGui();
                            })
            );

            default -> DisplayElement.filler();
        };
    }

    protected int getPageAmount() {
        return Math.min(maxPageNumber, ah.getSize() / PAGE_SIZE + 1);
    }

    private void openPublic() throws FileNotFoundException, UnsupportedEncodingException {
        this.close();
        GUIAuctionHouse gui = new GUIAuctionHouse(player);
        gui.updateDisplay();
        gui.setTitle(Text.literal("Auction House"));
        gui.open();
    }

    protected DisplayElement getElement(int id) {
        final int id1 = page * PAGE_SIZE + id;
        if (id1 >= ah.getSize()) {
            return null;
        }
        AuctionItem ai = ah.getItem(id1);
        return DisplayElement.of(
                GuiElementBuilder.from(ai.getItemStack())
                        .addLoreLine(Text.literal(ai.getTimeLeft()).formatted(Formatting.DARK_PURPLE))
                        .addLoreLine(Text.literal(String.format("%.2f $", ai.getPrice())).formatted(Formatting.DARK_PURPLE))
                        .setCallback((x, y, z) -> {
                            playClickSound(this.player);
                            try {
                                openItemGui(ah.getItem(id1));
                            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                                throw new RuntimeException(e);
                            }
                        }));
    }

    @Override
    public void onTick() {
        ticker++;
        if (ticker >= 20) {
            ticker = 0;
            ah = AuctionHouseMain.ah.getPlayerAuctionHouse(player.getUuidAsString());
            updateDisplay();
        }

        super.onTick();
    }

    private void openItemGui(AuctionItem item) throws FileNotFoundException, UnsupportedEncodingException {
        this.close();
        GUIAuctionItem gui = new GUIAuctionItem(player, item);
        gui.updateDisplay();
        gui.setTitle(Text.literal("Buy"));
        gui.open();
    }

    private void openExpiredGui() {
        this.close();
        GUIExpiredItems gui = new GUIExpiredItems(player);
        gui.updateDisplay();
        gui.setTitle(Text.literal("Expired Items"));
        gui.open();
    }

    private static void playClickSound(ServerPlayerEntity player) {
        player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 1, 1);
    }

    public record DisplayElement(@Nullable GuiElementInterface element, @Nullable Slot slot) {
        private static final DisplayElement EMPTY = DisplayElement.of(new GuiElement(ItemStack.EMPTY, GuiElementInterface.EMPTY_CALLBACK));
        private static final DisplayElement FILLER = DisplayElement.of(
                new GuiElementBuilder(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                        .setName(Text.literal(""))
                        .hideFlags()
        );

        public static DisplayElement of(GuiElementInterface element) {
            return new DisplayElement(element, null);
        }

        public static DisplayElement of(GuiElementBuilderInterface<?> element) {
            return new DisplayElement(element.build(), null);
        }

        public static DisplayElement nextPage(GUIPersonalAuctionHouse gui) {
            if (gui.canNextPage()) {
                return DisplayElement.of(
                        new GuiElementBuilder(Items.PLAYER_HEAD)
                                .setName(Text.translatable("spectatorMenu.next_page").formatted(Formatting.WHITE))
                                .hideFlags()
                                .setSkullOwner(HeadTextures.GUI_NEXT_PAGE)
                                .setCallback((x, y, z) -> {
                                    playClickSound(gui.player);
                                    gui.nextPage();
                                }));
            } else {
                return DisplayElement.of(
                        new GuiElementBuilder(Items.PLAYER_HEAD)
                                .setName(Text.translatable("spectatorMenu.next_page").formatted(Formatting.DARK_GRAY))
                                .hideFlags()
                                .setSkullOwner(HeadTextures.GUI_NEXT_PAGE_BLOCKED));
            }
        }

        public static DisplayElement previousPage(GUIPersonalAuctionHouse gui) {
            if (gui.canPreviousPage()) {
                return DisplayElement.of(
                        new GuiElementBuilder(Items.PLAYER_HEAD)
                                .setName(Text.translatable("spectatorMenu.previous_page").formatted(Formatting.WHITE))
                                .hideFlags()
                                .setSkullOwner(HeadTextures.GUI_PREVIOUS_PAGE)
                                .setCallback((x, y, z) -> {
                                    playClickSound(gui.player);
                                    gui.previousPage();
                                }));
            } else {
                return DisplayElement.of(
                        new GuiElementBuilder(Items.PLAYER_HEAD)
                                .setName(Text.translatable("spectatorMenu.previous_page").formatted(Formatting.DARK_GRAY))
                                .hideFlags()
                                .setSkullOwner(HeadTextures.GUI_PREVIOUS_PAGE_BLOCKED));
            }
        }

        public static DisplayElement filler() {
            return FILLER;
        }

        public static DisplayElement empty() {
            return EMPTY;
        }
    }
}
