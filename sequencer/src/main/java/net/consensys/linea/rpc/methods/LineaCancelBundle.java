package net.consensys.linea.rpc.methods;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;
import net.consensys.linea.rpc.services.LineaLimitedBundlePool;
import org.hyperledger.besu.ethereum.api.jsonrpc.internal.parameters.JsonRpcParameter;
import org.hyperledger.besu.plugin.services.RpcEndpointService;
import org.hyperledger.besu.plugin.services.exception.PluginRpcEndpointException;
import org.hyperledger.besu.plugin.services.rpc.PluginRpcRequest;

@Slf4j
public class LineaCancelBundle {
  private static final AtomicInteger LOG_SEQUENCE = new AtomicInteger();
  private final JsonRpcParameter parameterParser = new JsonRpcParameter();
  private final RpcEndpointService rpcEndpointService;
  private final LineaLimitedBundlePool bundlePool;

  public LineaCancelBundle(
      final RpcEndpointService rpcEndpointService, LineaLimitedBundlePool bundlePool) {
    this.rpcEndpointService = rpcEndpointService;
    this.bundlePool = bundlePool;
  }

  public String getNamespace() {
    return "linea";
  }

  public String getName() {
    return "cancelBundle";
  }

  public Boolean execute(final PluginRpcRequest request) {
    // sequence id for correlating error messages in logs:
    final int logId = log.isDebugEnabled() ? LOG_SEQUENCE.incrementAndGet() : -1;
    try {
      final UUID replacementUUID = parameterParser.required(request.getParams(), 0, UUID.class);

      return bundlePool.remove(replacementUUID);

    } catch (final Exception e) {
      log.atError()
          .setMessage("[{}] failed to parse linea_cancelBundle request")
          .addArgument(logId)
          .setCause(e)
          .log();
      throw new PluginRpcEndpointException(
          new LineaSendBundle.LineaSendBundleError("malformed linea_cancelBundle json param"));
    }
  }
}
