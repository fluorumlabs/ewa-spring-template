import {css, html, LitElement, property} from 'lit-element';
import '@vaadin/vaadin-ordered-layout';
import '@vaadin/vaadin-text-field';
import '@vaadin/vaadin-button';
import '@vaadin/vaadin-notification';

import {RestControllerClient} from "./rest/clients";
import defaultHttpClient from "./rest/DefaultHttpClient";

const restControllerClient = new RestControllerClient(defaultHttpClient);

export class ExampleApplication extends LitElement {
    @property({type: String}) userName = '';

    static styles = css`
		:host {
			min-height: 100vh;
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: flex-start;
			max-width: 960px;
			margin: 0 auto;
			text-align: center;
		}

		main {
			flex-grow: 1;
			display: flex;
  			align-items: center;
		}
	`;

    render() {
        return html`
			<main>
				<div>
					<vaadin-text-field label="What's your name, mate" @value-changed="${this.userNameChanged}"></vaadin-text-field>
					<vaadin-button ?disabled="${'' == this.userName}" @click="${this.sendRegards}">Send regards</vaadin-button>
				</div>			
			</main>
			<vaadin-notification theme="primary" id="notification" duration="4000"></vaadin-notification>
		`;
    }

    userNameChanged(ev: CustomEvent) {
        this.userName = ev.detail.value;
    }

    showNotification(text: string) {
        const notification = document.createElement('vaadin-notification');
        notification.setAttribute('theme', 'primary');
        notification.setAttribute('duration', '4000');
        notification.setAttribute('opened', '');
        notification.renderer = ((root) => root.textContent = text);
        //FIXME There has to be a better way :D
        this.appendChild(notification);
        this.removeChild(notification);
    }

    async sendRegards() {
        this.showNotification(await restControllerClient.hello({name: this.userName}));
    }


}
