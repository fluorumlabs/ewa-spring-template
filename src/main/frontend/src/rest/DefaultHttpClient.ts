import {HttpClient, RestResponse} from "./clients";

export class ApiError extends Error {
    readonly status: number;

    constructor(status: number, message: string) {
        super(message);
        this.name = "ApiError";
        this.status = status;
    }
}

class DefaultHttpClient implements HttpClient {
    request<R>(requestConfig: { method: string; url: string; queryParams?: any; data?: any; copyFn?: (data: R) => R; }): RestResponse<R> {
        const isGet = requestConfig.method === 'GET';
        const hasData = requestConfig.data !== null && requestConfig.data !== undefined;
        const hasParams = requestConfig.queryParams !== null && requestConfig.queryParams !== undefined;
        let url: string;

        const requestInit: RequestInit = {
            method: requestConfig.method
        };

        if (isGet) {
            url = requestConfig.url + (hasParams ? '?' + new URLSearchParams(requestConfig.queryParams).toString() : '');
        } else {
            url = requestConfig.url + ((hasParams && hasData) ? '?' + new URLSearchParams(requestConfig.queryParams).toString() : '');

            if (hasData) {
                if (typeof requestConfig.data === 'string' || requestConfig.data instanceof String) {
                    requestInit.headers = {
                        'Content-Type': 'application/octet-stream',
                    };
                    requestInit.body = <string>requestConfig.data;
                } else {
                    requestInit.headers = {
                        'Content-Type': 'application/json'
                    };
                    requestInit.body = JSON.stringify(requestConfig.data);
                }
            } else if (hasParams && !hasData) {
                requestInit.headers = {
                    'Content-Type': 'application/x-www-form-urlencoded',
                };
                requestInit.body = new URLSearchParams(requestConfig.queryParams).toString();
            }
        }

        return fetch(url, requestInit).then(response => {
            if (!response.ok) {
                throw new ApiError(response.status, response.statusText);
            }

            const contentType = response.headers.get("content-type");
            if (contentType && contentType.indexOf("application/json") !== -1) {
                return requestConfig.copyFn ? response.json().then(requestConfig.copyFn) : response.json();
            } else {
                return response.text();
            }
        });
    }
}

const defaultHttpClient = new DefaultHttpClient();

export {defaultHttpClient};
export default defaultHttpClient;
