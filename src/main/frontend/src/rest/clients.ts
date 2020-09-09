/* tslint:disable */
/* eslint-disable */

export interface HttpClient {

    request<R>(requestConfig: { method: string; url: string; queryParams?: any; data?: any; copyFn?: (data: R) => R; }): RestResponse<R>;
}

export class RestControllerClient {

    constructor(protected httpClient: HttpClient) {
    }

    /**
     * HTTP POST /api/hello
     * Java method: com.example.demo.RestController.hello
     */
    hello(queryParams: { name: string; }): RestResponse<string> {
        return this.httpClient.request({ method: 'POST', url: uriEncoding`api/hello`, queryParams: queryParams });
    }
}

export type RestResponse<R> = Promise<R>;

function uriEncoding(template: TemplateStringsArray, ...substitutions: any[]): string {
    let result = '';
    for (let i = 0; i < substitutions.length; i++) {
        result += template[i];
        result += encodeURIComponent(substitutions[i]);
    }
    result += template[template.length - 1];
    return result;
}
