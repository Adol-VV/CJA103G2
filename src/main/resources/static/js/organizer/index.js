import Navigation from './core/navigation.js';
import Sidebar from './core/sidebar.js';
import Forms from './core/forms.js';
import UI from './core/ui.js';
import { loadPartials } from '../modules/partial-loader.js';

// Organizer Granular Modules
import { checkOrganizerAuth } from './modules/auth.js';
import { initDashboardCharts } from './modules/charts.js';
import { initNotifications } from './modules/notifications.js';
import { initMemberNotify } from './modules/member-notify.js';
import { initEventList } from './modules/event-list.js';
import { initEventCreate } from './modules/event-create.js';
import { initEventEdit } from './modules/event-edit.js';
import { initOrderManagement } from './modules/orders.js';
import { initScanner } from './modules/scanner.js';
import { initProductList } from './modules/product-list.js';
import { initProductCreate } from './modules/product-create.js';
import { initProductOrders } from './modules/product-orders.js';
import { initArticleManagement } from './modules/articles.js';
import { initReports } from './modules/reports.js';
import { initSettlement } from './modules/settlement.js';
import { initSettings } from './modules/settings.js';

document.addEventListener('DOMContentLoaded', async () => {
    // 1. Auth Check
    checkOrganizerAuth();

    // 2. Load Partials first
    await loadPartials();

    // 3. Initialize Core Modules
    window.Navigation = Navigation;
    window.Sidebar = Sidebar;
    window.Forms = Forms;
    window.UI = UI;

    Navigation.init();
    Sidebar.init();
    Forms.init();
    UI.init();

    // 4. Initialize All Functional Modules
    initDashboardCharts();
    initNotifications();
    initMemberNotify();
    initEventList();
    initEventCreate();
    initEventEdit();
    initOrderManagement();
    initScanner();
    initProductList();
    initProductCreate();
    initProductOrders();
    initArticleManagement();
    initReports();
    initSettlement();
    initSettings();

    console.log('Organizer Dashboard Fully Modularized');
});
