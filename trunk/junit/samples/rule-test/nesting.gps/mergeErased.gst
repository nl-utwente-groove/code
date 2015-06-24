<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="mergeErased">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n138212">
            <attr name="layout">
                <string>290 125 18 21</string>
            </attr>
        </node>
        <node id="n138210">
            <attr name="layout">
                <string>290 272 18 21</string>
            </attr>
        </node>
        <node id="n138213">
            <attr name="layout">
                <string>128 190 441 21</string>
            </attr>
        </node>
        <node id="n138209">
            <attr name="layout">
                <string>259 46 408 21</string>
            </attr>
        </node>
        <node id="n138211">
            <attr name="layout">
                <string>405 126 17 21</string>
            </attr>
        </node>
        <edge to="n138210" from="n138213">
            <attr name="label">
                <string>A-node</string>
            </attr>
        </edge>
        <edge to="n138211" from="n138209">
            <attr name="label">
                <string>merged node</string>
            </attr>
        </edge>
        <edge to="n138209" from="n138209">
            <attr name="label">
                <string>rem:</string>
            </attr>
        </edge>
        <edge to="n138212" from="n138213">
            <attr name="label">
                <string>A-node</string>
            </attr>
        </edge>
        <edge to="n138213" from="n138213">
            <attr name="label">
                <string>rem:</string>
            </attr>
        </edge>
        <edge to="n138213" from="n138213">
            <attr name="label">
                <string>Two nodes, which will be matched by both the top level A and the sublevel A</string>
            </attr>
        </edge>
        <edge to="n138210" from="n138210">
            <attr name="label">
                <string>A</string>
            </attr>
        </edge>
        <edge to="n138212" from="n138212">
            <attr name="label">
                <string>A</string>
            </attr>
        </edge>
        <edge to="n138211" from="n138211">
            <attr name="label">
                <string>B</string>
            </attr>
        </edge>
        <edge to="n138209" from="n138209">
            <attr name="label">
                <string>The node to be merged with A on the sublevel (and therefore deleted)</string>
            </attr>
        </edge>
    </graph>
</gxl>
