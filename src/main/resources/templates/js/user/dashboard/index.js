/**
 * User Dashboard Entry Point
 * Aggregates all user dashboard modules.
 */

import { loadPartials } from '../../modules/partial-loader.js';
import Nav from './nav.js';
import Wallet from './wallet.js';
import Tickets from './tickets.js';
import Orders from './orders.js';
import Comments from './comments.js';
import { initSettings } from './settings.js';
import { initFavorites } from './favorites.js';
import { initNotifications } from './notifications.js';
import { initOverview } from './overview.js';

document.addEventListener('DOMContentLoaded', async () => {
    // 1. Load HTML Partials first
    await loadPartials();

    // 2. Initialize Dashboard Modules now that DOM is ready
    Nav.init();
    Wallet.init();
    Tickets.init();
    Orders.init();
    Comments.init();

    // Remaining modularized sections
    initSettings();
    initFavorites();
    initNotifications();
    initOverview();

    console.log('User Dashboard Modules Fully Loaded');
});
