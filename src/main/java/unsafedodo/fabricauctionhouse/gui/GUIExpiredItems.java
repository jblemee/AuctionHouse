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
import unsafedodo.fabricauctionhouse.auction.AuctionItem;
import unsafedodo.fabricauctionhouse.auction.ExpiredItems;
import unsafedodo.fabricauctionhouse.config.ConfigManager;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class GUIExpiredItems extends SimpleGui {
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
    private ExpiredItems expired;

    public <T extends  GUIExpiredItems> GUIExpiredItems(ServerPlayerEntity player) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);
        this.setTitle(Text.literal("Expired Items"));
        expired = AuctionHouseMain.ei.getPlayerExpiredItems(player.getUuidAsString());
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
                            .setCallback((index, type1, action) -> {
                                playClickSound(this.player);
                                this.close();
                            })
            );
            case 5 -> DisplayElement.nextPage(this);
            default -> DisplayElement.filler();
        };
    }

    protected int getPageAmount() {
        return Math.min(maxPageNumber, expired.size() / PAGE_SIZE + 1);
    }

    protected DisplayElement getElement(int id) {
        final int id1 = page * PAGE_SIZE + id;
        if (id1 >= expired.size()) {
            return null;
        }
        return DisplayElement.of(
                GuiElementBuilder.from(expired.getItem(id1).getItemStack())
                        .setCallback((x, y, z) -> {
                            playClickSound(this.player);
                            collectItem(expired.getItem(id1));
                        }));
    }

    private void collectItem(AuctionItem item) {
        if (player.getInventory().getEmptySlot() != -1) {
            AuctionHouseMain.getDatabaseManager().removeItemFromExpired(item);
            expired.removeItem(item);
            player.getInventory().offerOrDrop(item.getItemStack());
            updateDisplay();
        }
    }

    private void openPublic() throws FileNotFoundException, UnsupportedEncodingException {
        this.close();
        GUIAuctionHouse gui = new GUIAuctionHouse(player);
        gui.updateDisplay();
        gui.setTitle(Text.literal("Auction House"));
        gui.open();
    }

    @Override
    public void onTick() {
        ticker++;
        if (ticker >= 100) {
            expired = AuctionHouseMain.ei.getPlayerExpiredItems(player.getUuidAsString());
            ticker = 0;
            updateDisplay();
        }

        super.onTick();
    }

    public static void playClickSound(ServerPlayerEntity player) {
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

        public static DisplayElement nextPage(GUIExpiredItems gui) {
            if (gui.canNextPage()) {
                return DisplayElement.of(
                        new GuiElementBuilder(Items.PLAYER_HEAD)
                                .setName(Text.translatable("spectatorMenu.next_page").formatted(Formatting.WHITE))
                                .hideFlags()
                                .setSkullOwner(HeadTextures.GUI_NEXT_PAGE)
                                .setCallback((index, type1, action) -> {
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

        public static DisplayElement previousPage(GUIExpiredItems gui) {
            if (gui.canPreviousPage()) {
                return DisplayElement.of(
                        new GuiElementBuilder(Items.PLAYER_HEAD)
                                .setName(Text.translatable("spectatorMenu.previous_page").formatted(Formatting.WHITE))
                                .hideFlags()
                                .setSkullOwner(HeadTextures.GUI_PREVIOUS_PAGE)
                                .setCallback((index, type1, action) -> {
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
