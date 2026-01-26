import Navigation from './core/navigation.js';
import Sidebar from './core/sidebar.js';
import Forms from './core/forms.js';
import UI from './core/ui.js';
import { loadPartials } from '../modules/partial-loader.js';

// Admin Granular Modules

import { initAnalytics } from './modules/analytics.js';

// Approvals
import { initEventApprovals } from './modules/event-approvals.js';
import { initProductApprovals } from './modules/product-approvals.js';
import { initOrganizerReviews } from './modules/organizer-reviews.js';

// Orders & Finance
import { initEventOrders } from './modules/event-orders.js';
import { initSettlement } from './modules/settlement.js';

// Users
import { initMemberList } from './modules/member-list.js';

// Content
import { initHomepageEditor } from './modules/homepage-editor.js';
import { initFaqManagement } from './modules/faq-management.js';

document.addEventListener('DOMContentLoaded', async () => {


    // 2. Load Partials
    await loadPartials();

    // 3. Initialize Core
    Navigation.init();
    Sidebar.init();
    Forms.init();
    UI.init();

    // 4. Initialize All Admin Functional Modules
    initAnalytics();
    initEventApprovals();
    initProductApprovals();
    initOrganizerReviews();
    initEventOrders();
    initSettlement();
    initMemberList();
    initHomepageEditor();
    initFaqManagement();

    console.log('Admin Dashboard Fully Modularized');
});
