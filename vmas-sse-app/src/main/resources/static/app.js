
const source = new EventSource("/sse/stream");

function addCustomer(p) {
    console.log("addCustomer", p);
}

function updateVisitStatus(id, status) {
    console.log("updateVisitStatus", id, status);
}

const handlers = {
    CUSTOMER_CREATED: (payload) => addCustomer(payload),
    VISIT_STATUS_CHANGED: (payload) =>
        updateVisitStatus(payload.visitId, payload.newStatus)
};

source.onmessage = (event) => {
    const msg = JSON.parse(event.data);

    const handler = handlers[msg.type];

    if (handler) handler(msg.payload);
};
