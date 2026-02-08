class AgendaContext {
    constructor(agendaState) {
        this.event = null
        this.agendaState = agendaState
        this.addContextMenu()
        this.addClick()
    }

    setEvent(event) {
        this.event = event;
    }

    addContextMenu() {
        window.addEventListener('contextmenu', (e) => {

            if (this.isNoContext())
                return;
            e.preventDefault();

            if (document.querySelector('.contextmenu-container')) {
                document.querySelector('.contextmenu-container').remove()
            }
            this.createMenu(this.createMenuOptions(this.event.event.extendedProps.contextStatus), this.event);
        });
    }

    addClick() {
        window.addEventListener('click', (e) => {
            if (document.querySelector('.contextmenu-container')) {
                document.querySelector('.contextmenu-container').remove()
            }
        })
    }

    isNoContext() {
        if (this.event === null) return true
        return this.event.event.extendedProps.contextStatus === "no-context";
    }

    createMenuOptions(contextStatus) {
        return ['cancel', contextStatus]
    }

    createMenu(menuOptions, event) {
        let menuContainer = document.createElement('div');
        menuContainer.classList.add("contextmenu-container");
        menuContainer.id = "contextmenu-container";

        menuOptions.forEach((option, index) => {
            let menuItem = document.createElement('div');
            menuItem.classList.add("contextmenu-item");

            let menuContent = document.createElement('a');
            menuContent.textContent = option;

            menuItem.append(menuContent);
            menuItem.addEventListener('click', async (e) => {
                e.preventDefault();
                e.stopPropagation();

                let resultData = await this.updateAppointmentVisit(event.event.extendedProps.visitId, option)
                console.log(resultData);
                if (resultData.length === 1) {
                    agendaState.calendar.updateEvent(resultData[0]);
                }
                document.getElementById("contextmenu-container").remove()
            });
            menuContainer.appendChild(menuItem);
        });
        event.el.appendChild(menuContainer);
    }

    async updateAppointmentVisit(visitId, option) {
        const formData = new URLSearchParams();
        formData.append('visitId', visitId);
        formData.append('option', option);
        formData.append('agendaType', agendaState.agendaType);
        formData.append('localMemberId', agendaState.localMemberId);

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