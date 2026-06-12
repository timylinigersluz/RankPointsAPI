package ch.ksrminecraft.RankPointsAPI;

import java.util.UUID;

/**
 * Public Bukkit service for RankPointsAPI integrations.
 * Database access, table management and staff exclusion are handled internally
 * by the RankPointsAPI server plugin.
 */
public interface RankPointsService {

    /**
     * Adds points to the given player UUID.
     * Staff exclusion and database handling are managed internally by RankPointsAPI.
     * Negative values are allowed and subtract points.
     *
     * @param uuid player UUID
     * @param delta amount of points to add
     */
    void addPoints(UUID uuid, int delta);

    /**
     * Sets the point total for the given player UUID.
     * Staff exclusion and database handling are managed internally by RankPointsAPI.
     *
     * @param uuid player UUID
     * @param points new point total
     */
    void setPoints(UUID uuid, int points);

    /**
     * Returns the current point total for the given player UUID.
     * Returns 0 if the player has no entry or if the point lookup fails.
     *
     * @param uuid player UUID
     * @return current point total
     */
    int getPoints(UUID uuid);

    /**
     * Removes points from the given player UUID.
     * Staff exclusion and database handling are managed internally by RankPointsAPI.
     *
     * @param uuid player UUID
     * @param delta positive amount of points to remove
     */
    void removePoints(UUID uuid, int delta);
}
