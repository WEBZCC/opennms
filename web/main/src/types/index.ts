import { Commit, Dispatch } from 'vuex'

export interface VuexContext {
  commit: Commit,
  dispatch: Dispatch
}

export interface ApiResponse {
  count: number
  offset: number
  totalCount: number
}

export interface NodeApiResponse extends ApiResponse {
  node: Node[]
}

export interface EventApiResponse extends ApiResponse {
  event: Event[]
}

export interface SnmpInterfaceApiResponse extends ApiResponse {
  snmpInterface: SnmpInterface[]
}

export interface IpInterfaceApiResponse extends ApiResponse {
  ipInterface: IpInterface[]
}

export interface OutagesApiResponse extends ApiResponse {
  outage: Outage[]
}

export interface LocationsApiResponse extends ApiResponse {
  location: MonitoringLocation[]
}

export interface IfServiceApiResponse extends ApiResponse {
  "monitored-service": IfService[]
}

export interface Node {
  location: string
  type: string
  label: string
  id: string
  assetRecord: any
  categories: Category[]
  createTime: number
  foreignId: string
  foreignSource: string
  lastEgressFlow: any
  lastIngressFlow: any
}

export interface Event {
  createTime: number
  description: string
  display: string
  id: number
  label: string
  location: string
  log: string
  logMessage: string
  nodeId: number
  nodeLabel: string
  parameters: Array<any>
  severity: string
  source: string
  time: number
  uei: string
}

export interface SnmpInterface {
  collect: boolean
  collectFlag: string
  collectionUserSpecified: boolean
  hasEgressFlows: boolean
  hasFlows: boolean
  hasIngressFlows: boolean
  id: number
  ifAdminStatus: number
  ifAlias: any
  ifDescr: any
  ifIndex: number
  ifName: any
  ifOperStatus: number
  ifSpeed: number
  ifType: number
  lastCapsdPoll: number
  lastEgressFlow: any
  lastIngressFlow: any
  lastSnmpPoll: number
  physAddr: any
  poll: boolean
}

export interface IpInterface {
  ifIndex: string
  isManaged: null | string
  id: string
  ipAddress: string
  isDown: boolean
  lastCapsdPoll: number
  lastEgressFlow: any
  lastIngressFlow: any
  monitoredServiceCount: number
  nodeId: number
  snmpInterface: SnmpInterface
  snmpPrimary: string
  hostName: string
}

export interface Outage {
  nodeId: number,
  ipAddress: string,
  serviceIs: number,
  nodeLabel: string
  location: string
  hostname: string
  serviceName: string
  outageId: number
}

export interface IfService {
  id: string
  ipAddress: string
  ipInterfaceId: number
  isDown: false
  isMonitored: boolean
  node: string
  serviceName: string
  status: string
  statusCode: string
}

export interface Category {
  authorizedGroups: string[]
  id: number
  name: string
}

export interface QueryParameters {
  limit?: number
  offset?: number
  _s?: string
  orderBy?: string
  order?: 'asc' | 'desc'
  [x: string]: any 
}

export interface SortProps {
  filters: Object
  first: Number
  multiSortMeta: Object
  originalEvent: MouseEvent
  rows: Number
  sortField: string
  sortOrder: 1 | -1
}

export interface NodeAvailability {
  availability: number
  id: number
  ipinterfaces: {
    address: string
    availability: number
    id: number
    services: [{
      id: number, 
      name: string, 
      availability: number
    }]
  }[]
  'service-count': number
  'service-down-count': number
}

export interface MonitoringLocation {
  geolocation: any
  latitude: any
  longitude: any
  priority: 100
  tags: any[]
  "location-name": string
  "monitoring-area": string
}

export interface IPRange {
  location: string
  startIP: string
  endIP: string
}

export interface IPRangeResponse {
  location: string
  scanResults: IPRangeResponseObject[]
}

interface IPRangeResponseObject {
  hostname: string
  ipAddress: string
  rtt: number
}

export interface SNMPDetectRequest {
  location: string
  ipAddresses: string[]
  configurations: SNMPDetectRequestConfig[]
}

export interface SNMPDetectRequestConfig {
  communityString: string
  timeout: number
  retry: number
  securityLevel: number
}

export interface SNMPDetectResponse {
  location: string
  hostname: string
  ipAddress: string
  sysOID: string | null
  communityString: string
  hasSNMPService: boolean
}

export interface ProvisionRequest {
  batchName: string
  scheduleTime: number
  discoverIPRanges: IPRange[]
  snmpConfigList: SNMPDetectRequest[]
}
