export interface SourceResponse {
  id: number;
  name: string;
  url: string;
  type: string;
  priority: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateSourceRequest {
  name: string;
  url: string;
  type: string;
  priority: number;
  active: boolean;
}

export type UpdateSourceRequest = CreateSourceRequest;