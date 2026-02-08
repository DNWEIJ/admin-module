class AgendaState {

    constructor(agendaType, currentDate, currentView, localMemberId) {
        this.agendaType = agendaType     // "room" | "vet" | "week"
        this.currentDate = currentDate   // Date
        this.currentView = currentView   // "resource" | "week"
        this.localMemberId = localMemberId
    }

    setAgendaType(agendaType) {
        this.agendaType = agendaType
    }

    setCurrentDate(date) {
        this.currentDate = date
    }

    setCurrentView(view) {
        this.currentView = view
    }

    setLocalMemberId(localMemberId) {
        this.localMemberId = localMemberId
    }

    setCalendar(calendar) {
        this.calendar = calendar
    }

    setNewAppointment(info) {
        // todo reduce to only dateStr?
        this.newAppointmentInfo = info
    }

    handleReturnModalSubmitForPetSelect(event) {
        // destroy modal
        document.getElementById('modal-container-agenda').innerHTML = ''
        document.getElementById('modal-container').innerHTML = ''

        // reset
        let element = document.getElementById('modalCommunication')
        element.setAttribute('hx-get', element.dataset.originalurl)
        element.setAttribute('x-on::after-request', element.dataset.originalafterrequest)


        // get event data for appointmentId
        console.log(event)
        const formData = new URLSearchParams();
        formData.append('agendaType', agendaState.agendaType);
        formData.append('localMemberId', agendaState.localMemberId);
        formData.append('start', agendaState.calendar.getOption('date').toISOString().split('T')[0] + 'T00:00:00');
        formData.append('end', agendaState.calendar.getOption('date').toISOString().split('T')[0] + 'T00:00:00');

        fetch('/agenda/appointment/' + event.detail.xhr.responseText, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: formData.toString()
        }).then(response => response.json()).then(data => {
            console.log(data[0])
            calendar.addEvent(data[0]);
            console.log(calendar.getEvents());
        });
    }

    handleModalSubmitForPetSelect(customerId) {
        let inputField = document.getElementById('modalCommunication')
        let urlQueryParams = '?' +
            'agendaType=' + this.agendaType + '&' +
            'resource=' + this.newAppointmentInfo.resource.id + '&' +
            'date=' + this.newAppointmentInfo.dateStr + '&' +
            'localMemberId=' + this.localMemberId

        let url = inputField.dataset.originalurl + '/' + customerId + urlQueryParams + '&isHtmx=true'

        inputField.setAttribute('hx-get', url)
        inputField.setAttribute('hx-on::after-request', 'addEventListenersForPetSelect()')
        htmx.process(inputField);
        htmx.trigger(inputField, 'change')
    }

    changeLocation(localMemberId) {
        this.setLocalMemberId(localMemberId)
        calendar.refetchEvents()
    }

    changeResourceType(resource) {
        this.setAgendaType(resource)
        calendar.refetchEvents()
        calendar.refetchResources()
    }

    moveDate(option) {
        let calendarDate = this.calendar.getOption('date');
        if (option == '>') {
            calendarDate.setDate(calendarDate.getDate() + 1)
            this.changeDate(this.formatDate(calendarDate))
        }
        if (option == '<') {
            calendarDate.setDate(calendarDate.getDate() - 1)
            this.changeDate(this.formatDate(calendarDate))
        }
        if (option == 'today') {
            this.changeDate(this.formatDate(new Date()))
        }
    }

    changeDate(changeDateTo) {
        this.calendar.setOption('date', changeDateTo);
        document.getElementById('selectDate').value = changeDateTo;
    }

    formatDate(date) {
        return date.getFullYear() + '-' +
            String(date.getMonth() + 1).padStart(2, '0') + '-' +
            String(date.getDate()).padStart(2, '0')
    }

    processData(event) {
        console.log(event)
        // fix input for event click
        // update event
        //
    }
}

// ensure we always send the csrf back
const
    originalFetch = window.fetch
window
    .fetch = function (input, init = {}) {
    const token = document.querySelector('meta[name="_csrf"]')?.content
    const header = document.querySelector('meta[name="_csrf_header"]')?.content

    init.headers = new Headers(init.headers || {})

    if (token && header) {
        init.headers.set(header, token)
    }
    return originalFetch(input, init)

}

function

scrollNowIndicator() {
    const el = document.querySelector('.ec-now-indicator');
    if (el) {
        el.scrollIntoView({
            behavior: 'smooth',
            block: 'center'
        });
    }
}
