package unsafedodo.fabricauctionhouse.gui;

import eu.pb4.sgui.api.gui.SimpleGui;
import eu.pb4.sgui.api.elements.*;
import eu.pb4.sgui.api.gui.*;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.network.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import unsafedodo.fabricauctionhouse.AuctionHouseMain;
import unsafedodo.fabricauctionhouse.auction.AuctionItem;
import unsafedodo.fabricauctionhouse.config.ConfigManager;
import unsafedodo.fabricauctionhouse.util.EconomyTransactionHandler;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;


public class GUIAuctionHouse extends SimpleGui {
    public static final int PAGE_SIZE = 45; //9x5

    public static int maxPageNumber;

    static {
        try {
            maxPageNumber = ConfigManager.getConfigData(ConfigManager.configFile).getAuctionHouseMaxPages();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    protected int page = 0;
    private int ticker = 0;

    public <T extends GUIAuctionHouse> GUIAuctionHouse(ServerPlayerEntity player) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);
        this.setTitle(Text.literal("Auction House"));
        try{
            this.updateDisplay();
        } catch (FileNotFoundException | UnsupportedEncodingException e){
            e.printStackTrace();
        }

    }

    protected void nextPage() throws FileNotFoundException, UnsupportedEncodingException {
        this.page = Math.min(this.getPageAmount() - 1, this.page + 1);
        this.updateDisplay();
    }

    protected boolean canNextPage() throws FileNotFoundException, UnsupportedEncodingException {
        return this.getPageAmount() > this.page + 1;
    }

    protected void previousPage() throws FileNotFoundException, UnsupportedEncodingException {
        this.page = Math.max(0, this.page - 1);
        this.updateDisplay();
    }

    protected boolean canPreviousPage() {
        return this.page - 1 >= 0;
    }

    public void updateDisplay() throws FileNotFoundException, UnsupportedEncodingException {
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

    protected DisplayElement getNavElement(int id) throws FileNotFoundException, UnsupportedEncodingException {
        return switch (id) {
            case 0 -> skull();
            case 3 -> DisplayElement.previousPage(this);
            case 4 -> DisplayElement.of(
                    new GuiElementBuilder(Items.BARRIER)
                            .setName(Text.translatable("spectatorMenu.close").formatted(Formatting.RED))
                            .hideFlags()
                            .setCallback((index, type1, action) -> {
                                playClickSound(this.player);
                                this.close();
                            })
            );
            case 5 -> DisplayElement.nextPage(this);
            case 8 -> DisplayElement.of(
                    new GuiElementBuilder(Items.HOPPER)
                            .setName(Text.translatable("Expired Items").formatted(Formatting.RED))
                            .hideFlags()
                            .setCallback((index, type1, action) -> {
                                playClickSound(this.player);
                                this.openExpiredGui();
                            })
            );

            default -> DisplayElement.filler();
        };
    }

    protected int getPageAmount() throws FileNotFoundException, UnsupportedEncodingException {
        return Math.min(maxPageNumber, AuctionHouseMain.ah.getSize() / PAGE_SIZE + 1);
    }

    protected DisplayElement getElement(int id) {
        final int id1 = page * PAGE_SIZE + id;
        if (id1 >= AuctionHouseMain.ah.getSize()) {
            return null;
        }
        AuctionItem ai = AuctionHouseMain.ah.getItem(id1);
        return DisplayElement.of(
                GuiElementBuilder.from(ai.getItemStack())
                        .addLoreLine(Text.literal(ai.getTimeLeft()).formatted(Formatting.DARK_PURPLE))
                        .addLoreLine(Text.literal(String.format("%.2f $", ai.getPrice())).formatted(Formatting.DARK_PURPLE))
                        .setCallback((index, type1, action) -> {
                            playClickSound(this.player);
                            openItemGui(AuctionHouseMain.ah.getItem(id1));
                        }));
    }

    @Override
    public void onTick() {
        ticker++;
        if (ticker >= 20) {
            ticker = 0;
            try {
                updateDisplay();
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        super.onTick();
    }

    protected void openItemGui(AuctionItem item) {
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

    private void openPersonal() {
        this.close();
        GUIPersonalAuctionHouse gui = new GUIPersonalAuctionHouse(player);
        gui.updateDisplay();
        gui.setTitle(Text.literal("My Items"));
        gui.open();
    }

    private DisplayElement skull() {
        ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
        stack.getOrCreateNbt().putString("SkullOwner",  player.getName().getString());
        return DisplayElement.of(GuiElementBuilder.from(stack)
                .setName(Text.literal("My Items").formatted(Formatting.BLUE))
                .hideFlags()
                .setCallback((index, type1, action) -> {
                    playClickSound(this.player);
                    openPersonal();
                }));

    }

    protected static void playClickSound(ServerPlayerEntity player) {
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

        public static DisplayElement nextPage(GUIAuctionHouse gui) throws FileNotFoundException, UnsupportedEncodingException {
            if (gui.canNextPage()) {
                return DisplayElement.of(
                        new GuiElementBuilder(Items.PLAYER_HEAD)
                                .setName(Text.translatable("spectatorMenu.next_page").formatted(Formatting.WHITE))
                                .hideFlags()
                                .setSkullOwner(HeadTextures.GUI_NEXT_PAGE)
                                .setCallback((index, type1, action) -> {
                                    playClickSound(gui.player);
                                    try {
                                        gui.nextPage();
                                    } catch (FileNotFoundException | UnsupportedEncodingException e) {
                                        throw new RuntimeException(e);
                                    }
                                }));
            } else {
                return DisplayElement.of(
                        new GuiElementBuilder(Items.PLAYER_HEAD)
                                .setName(Text.translatable("spectatorMenu.next_page").formatted(Formatting.DARK_GRAY))
                                .hideFlags()
                                .setSkullOwner(HeadTextures.GUI_NEXT_PAGE_BLOCKED));
            }
        }

        public static DisplayElement previousPage(GUIAuctionHouse gui) {
            if (gui.canPreviousPage()) {
                return DisplayElement.of(
                        new GuiElementBuilder(Items.PLAYER_HEAD)
                                .setName(Text.translatable("spectatorMenu.previous_page").formatted(Formatting.WHITE))
                                .hideFlags()
                                .setSkullOwner(HeadTextures.GUI_PREVIOUS_PAGE)
                                .setCallback((x, y, z) -> {
                                    playClickSound(gui.player);
                                    try {
                                        gui.previousPage();
                                    } catch (FileNotFoundException | UnsupportedEncodingException e) {
                                        throw new RuntimeException(e);
                                    }
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
