class AgendaContext {
    static openInNewWindow = 'open in new window';

    constructor(agendaState) {
        this.event = null;
        this.agendaState = agendaState;
        this.contextContainer = document.getElementById('global-context');
        this.addContextMenu();
        this.addClick();
    }

    setEvent(event) {
        this.event = event;
    }

    addContextMenu() {
        window.addEventListener('contextmenu', (e) => {
            if (this.isNoContext()) return;

            e.preventDefault();

            // Clear previous menu items but keep container
            this.clearMenu();

            const menuOptions = this.createMenuOptions(this.event.event.extendedProps.contextStatus);
            this.createMenu(menuOptions, this.event, e.clientX, e.clientY);
        });
    }

    addClick() {
        window.addEventListener('click', () => {
            this.clearMenu();
        });
    }

    isNoContext() {
        return !this.event || this.event.event.extendedProps.contextStatus === "no-context";
    }

    createMenuOptions(contextStatus) {
        return ['cancel', contextStatus, '--', AgendaContext.openInNewWindow];
    }

    clearMenu() {
        while (this.contextContainer.firstChild) {
            this.contextContainer.removeChild(this.contextContainer.firstChild);
        }
    }

    createMenu(menuOptions, event, x, y) {
        const menuContainer = document.createElement('div');
        menuContainer.classList.add("contextmenu-container");
        menuContainer.style.position = 'absolute';
        menuContainer.style.display = 'block';
        menuContainer.style.zIndex = 9999;

        // Position the menu at the click
        menuContainer.style.top = `${y}px`;
        menuContainer.style.left = `${x}px`;

        menuOptions.forEach(option => {
            const menuItem = document.createElement('div');

            if (option.startsWith('-')) {
                const menuContent = document.createElement('div');
                menuContent.append(document.createElement('hr'));
                menuItem.append(menuContent);
            } else {
                menuItem.classList.add("contextmenu-item");
                const menuContent = document.createElement('a');
                menuContent.textContent = option;
                menuItem.append(menuContent);

                if (option === AgendaContext.openInNewWindow) {
                    menuItem.addEventListener('click', (e) => {
                        e.preventDefault();
                        e.stopPropagation();
                        this.clearMenu();
                        const props = event.event.extendedProps;
                        if (props.otc) {
                            window.open(`/sales/otc/customer/${props.customerId}/visit/${props.visitId}`, "_blank");
                        } else {
                            window.open(`/consult/visit/customer/${props.customerId}/visit/${props.visitId}?callFrom=agenda`, "_blank");
                        }
                    });
                } else {
                    menuItem.addEventListener('click', async (e) => {
                        e.preventDefault();
                        e.stopPropagation();
                        const resultData = await this.updateAppointmentVisit(event.event.extendedProps.visitId, option);
                        if (resultData.length === 1) {
                            this.agendaState.calendar.updateEvent(resultData[0]);
                        }
                        this.clearMenu();
                    });
                }
            }

            menuContainer.appendChild(menuItem);
        });

        this.contextContainer.appendChild(menuContainer);
        this.contextContainer.style.display = 'block';
    }

    async updateAppointmentVisit(visitId, option) {
        const formData = new URLSearchParams();
        formData.append('visitId', visitId);
        formData.append('option', option);
        formData.append('agendaType', agendaState.agendaType);
        formData.append('localMemberId', agendaState.localMemberId);
        formData.append('isList', calendar.getOption('view').startsWith('list'));

        formData.append('start', agendaState.calendar.getOption('date').toISOString().split('T')[0] + 'T00:00:00');
        formData.append('end', agendaState.calendar.getOption('date').toISOString().split('T')[0] + 'T00:00:00');

        const response = await fetch('/agenda/visit', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: formData.toString()
        });
        return response.json();
    }
}