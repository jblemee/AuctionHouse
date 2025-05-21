package co.lemee.auctionhouse.sql;

import co.lemee.auctionhouse.AuctionHouseMod;
import co.lemee.auctionhouse.auction.AuctionItem;
import co.lemee.auctionhouse.util.CommonMethods;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static co.lemee.auctionhouse.AuctionHouseMod.LOGGER;
import static co.lemee.auctionhouse.AuctionHouseMod.connection;

public class SQLiteDatabaseManager implements DatabaseManager {
    public static String url = "jdbc:sqlite:auctionhouse.db";

    public static void createTables(ArrayList<String> tableRegistry) {
        try (Statement stmt = connection.createStatement()) {
            for (String sql : tableRegistry) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to create tables", e);
        }
    }

    public static List<AuctionItem> getItemList() {
        String sql = "SELECT * FROM auctionhouse";
        List<AuctionItem> list = new ArrayList<>();
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new AuctionItem(rs.getInt("id"), rs.getString("playeruuid"), rs.getString("owner"), rs.getString("nbt"), rs.getString("item"), rs.getInt("count"), rs.getDouble("price"), rs.getLong("secondsLeft")));
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to get most recent ID", e);
        }

        return list;
    }

    public static List<AuctionItem> getExpiredItemsList() {
        String sql = "SELECT * FROM expireditems";
        List<AuctionItem> list = new ArrayList<>();

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new AuctionItem(rs.getInt("id"), rs.getString("playeruuid"), rs.getString("owner"), rs.getString("nbt"), rs.getString("item"), rs.getInt("count"), rs.getDouble("price"), 0));
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to get item id", e);
        }

        return list;
    }

    @Override
    public int addItemToAuction(String playeruuid, String owner, String nbt, String item, int count, double price, long secondsLeft) {
        String sql = "INSERT INTO auctionhouse(playeruuid,owner,nbt,item,count,price,secondsLeft) VALUES(?,?,?,?,?,?,?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playeruuid);
            pstmt.setString(2, owner);
            pstmt.setString(3, nbt);
            pstmt.setString(4, item);
            pstmt.setInt(5, count);
            pstmt.setDouble(6, price);
            pstmt.setLong(7, secondsLeft);
            pstmt.executeUpdate();
            CommonMethods.reloadHouse();
            return getMostRecentId();
        } catch (SQLException e) {
            LOGGER.error("Failed to add item to auction", e);
        }
        return -1;
    }

    @Override
    public int getMostRecentId() {
        String sql = "SELECT id FROM auctionhouse ORDER BY id DESC";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt("id");
        } catch (SQLException e) {
            LOGGER.error("Failed to get most recent ID", e);
        }
        return -1;
    }

    @Override
    public int playerItemCount(String playeruuid, String table) {
        String sql = "SELECT Count(*) FROM " + table + " WHERE playeruuid = '" + playeruuid + "'";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            LOGGER.error("Failed to get player item count", e);
        }
        return -1;
    }

    @Override
    public boolean isItemForAuction(int id) {
        String sql = "SELECT Count(id) FROM auctionhouse WHERE id = " + id;
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            LOGGER.error("Failed to check if item is for auction", e);
        }
        return false;
    }

    @Override
    public void updateTime(int id, long seconds) {
        String sql = "UPDATE auctionhouse SET secondsLeft = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, seconds);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Failed to update item time", e);
        }
    }

    @Override
    public void removeItemFromAuction(AuctionItem item) {
        String sql = "DELETE FROM auctionhouse WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, item.getId());
            pstmt.executeUpdate();
            AuctionHouseMod.ah.removeItem(item);
        } catch (SQLException e) {
            LOGGER.error("Failed to remove item from auction", e);
        }
    }

    @Override
    public void removeItemFromExpired(AuctionItem item) {
        AuctionHouseMod.ei.removeItem(item);
        String sql = "DELETE FROM expireditems WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, item.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Failed to remove item from expired", e);
        }
    }

    @Override
    public void expireItem(AuctionItem item) {
        removeItemFromAuction(item);
        AuctionHouseMod.ei.addItem(item);
        String sql = "INSERT INTO expireditems(id,playeruuid,owner,nbt,item,count,price) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, item.getId());
            pstmt.setString(2, item.getUuid());
            pstmt.setString(3, item.getOwner());
            pstmt.setString(4, item.getNbt());
            pstmt.setString(5, item.getName());
            pstmt.setInt(6, item.getItemStack().getCount());
            pstmt.setDouble(7, item.getPrice());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Failed to expire item", e);
        }
    }

}
