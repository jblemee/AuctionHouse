package co.lemee.auctionhouse.gui;

import co.lemee.auctionhouse.AuctionHouseMod;
import co.lemee.auctionhouse.auction.AuctionItem;
import co.lemee.auctionhouse.config.ConfigManager;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementBuilderInterface;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

import static co.lemee.auctionhouse.AuctionHouseMod.LOGGER;


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

    public <T extends GUIAuctionHouse> GUIAuctionHouse(ServerPlayer player) {
        super(MenuType.GENERIC_9x6, player, false);
        this.setTitle(Component.literal("Auction House"));
        try {
            this.updateDisplay();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            LOGGER.error("Error updating display", e);
        }

    }

    protected static void playClickSound(ServerPlayer player) {
        player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.MASTER, 1, 1);
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
                            .setName(Component.translatable("spectatorMenu.close").withStyle(ChatFormatting.RED))
                            .hideDefaultTooltip()
                            .setCallback((index, type1, action) -> {
                                playClickSound(this.player);
                                this.close();
                            })
            );
            case 5 -> DisplayElement.nextPage(this);
            case 8 -> DisplayElement.of(
                    new GuiElementBuilder(Items.HOPPER)
                            .setName(Component.translatable("Expired Items").withStyle(ChatFormatting.RED))
                            .hideDefaultTooltip()
                            .setCallback((index, type1, action) -> {
                                playClickSound(this.player);
                                this.openExpiredGui();
                            })
            );

            default -> DisplayElement.filler();
        };
    }

    protected int getPageAmount() throws FileNotFoundException, UnsupportedEncodingException {
        return Math.min(maxPageNumber, AuctionHouseMod.ah.getSize() / PAGE_SIZE + 1);
    }

    protected DisplayElement getElement(int id) {
        final int id1 = page * PAGE_SIZE + id;
        if (id1 >= AuctionHouseMod.ah.getSize()) {
            return null;
        }
        AuctionItem ai = AuctionHouseMod.ah.getItem(id1);
        return DisplayElement.of(
                GuiElementBuilder.from(ai.getItemStack())
                        .addLoreLine(Component.literal(ai.getTimeLeft()).withStyle(ChatFormatting.DARK_PURPLE))
                        .addLoreLine(Component.literal(String.format("%.2f $", ai.getPrice())).withStyle(ChatFormatting.DARK_PURPLE))
                        .setCallback((index, type1, action) -> {
                            playClickSound(this.player);
                            try {
                                openItemGui(AuctionHouseMod.ah.getItem(id1));
                            } catch (ExecutionException | InterruptedException e) {
                                throw new RuntimeException(e);
                            }
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

    protected void openItemGui(AuctionItem item) throws ExecutionException, InterruptedException {
        this.close();
        GUIAuctionItem gui = new GUIAuctionItem(player, item);
        gui.updateDisplay();
        gui.setTitle(Component.literal("Buy"));
        gui.open();
    }

    private void openExpiredGui() {
        this.close();
        GUIExpiredItems gui = new GUIExpiredItems(player);
        gui.updateDisplay();
        gui.setTitle(Component.literal("Expired Items"));
        gui.open();
    }

    private void openPersonal() {
        this.close();
        GUIPersonalAuctionHouse gui = new GUIPersonalAuctionHouse(player);
        gui.updateDisplay();
        gui.setTitle(Component.literal("My Items"));
        gui.open();
    }

    private DisplayElement skull() {
        ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
        return DisplayElement.of(GuiElementBuilder.from(stack)
                .setName(Component.literal("My Items").withStyle(ChatFormatting.BLUE))
                .hideDefaultTooltip()
                .setCallback((index, type1, action) -> {
                    playClickSound(this.player);
                    openPersonal();
                }));

    }

    public record DisplayElement(@Nullable GuiElementInterface element, @Nullable Slot slot) {
        private static final DisplayElement EMPTY = DisplayElement.of(new GuiElement(ItemStack.EMPTY, GuiElementInterface.EMPTY_CALLBACK));
        private static final DisplayElement FILLER = DisplayElement.of(
                new GuiElementBuilder(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                        .setName(Component.literal(""))
                        .hideDefaultTooltip()
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
                                .setName(Component.translatable("spectatorMenu.next_page").withStyle(ChatFormatting.WHITE))
                                .hideDefaultTooltip()
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
                                .setName(Component.translatable("spectatorMenu.next_page").withStyle(ChatFormatting.DARK_GRAY))
                                .hideDefaultTooltip()
                                .setSkullOwner(HeadTextures.GUI_NEXT_PAGE_BLOCKED));
            }
        }

        public static DisplayElement previousPage(GUIAuctionHouse gui) {
            if (gui.canPreviousPage()) {
                return DisplayElement.of(
                        new GuiElementBuilder(Items.PLAYER_HEAD)
                                .setName(Component.translatable("spectatorMenu.previous_page").withStyle(ChatFormatting.WHITE))
                                .hideDefaultTooltip()
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
                                .setName(Component.translatable("spectatorMenu.previous_page").withStyle(ChatFormatting.DARK_GRAY))
                                .hideDefaultTooltip()
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
